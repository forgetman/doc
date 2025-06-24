package feature.tts

import feature.tts.def.Error
import feature.tts.def.UtteranceProgressDispatcher
import feature.tts.engine.BaseTextToSpeechEngine
import feature.tts.manager.TextToSpeechManager
import feature.tts.model.Params
import feature.tts.model.SpeechItem
import feature.tts.model.SpeechItemWrapper
import feature.tts.service.TextToSpeechService
import feature.tts.stream.TextToSpeechInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import sugar.collection.safeMutableListOf
import vector.ext.dispatchEach

interface TextToSpeech {
    enum class Strategy {
        FLUSH, // 清空队列，立即播放
        INTERRUPT, // 中断当前播放的item，添加到队列头部并立即播放
        ADD_FIRST, // 添加到队列头部, 等待正在播放的item播放完毕后播放, 或者立即播放
        ADD, // 添加到队列尾部
        FORCE, // 强制播放, 优先级高的才能使用, 有INTERRUPT功能; 如果当前service处于暂停状态, 会先恢复service
    }

    fun speak(
        inputStream: TextToSpeechInputStream,
        params: Params,
        utteranceId: String?,
        strategy: Strategy = Strategy.ADD
    )

    fun cancel(utteranceId: String)
    fun cancelAll()

    interface UtteranceProgressListener {
        fun onStart(utteranceId: String) {}
        fun onEnd(utteranceId: String) {}
        fun onError(utteranceId: String, error: Error) {}
    }

    fun addUtteranceProgressListener(listener: UtteranceProgressListener)
    fun removeUtteranceProgressListener(listener: UtteranceProgressListener)

    fun release()
}

/**
 * @param priority 优先级，值越大优先级越高
 * @param engine 引擎
 * @param withGlobalQueue 是否使用全局队列, false: 独立播放, 不受全局队列影响(会出现同时播放的情况), 不受全局队列service暂停状态影响
 */
fun TextToSpeech(priority: Int, engine: BaseTextToSpeechEngine, withGlobalQueue: Boolean = true): TextToSpeech {
    return TextToSpeechImpl(priority, engine, withGlobalQueue)
}

private class TextToSpeechImpl(
    private val priority: Int,
    private val engine: BaseTextToSpeechEngine,
    private val withGlobalQueue: Boolean
) : TextToSpeech {

    companion object {
        private const val UTTERANCE_ID_PREFIX = "TextToSpeech_internalId_"
    }

    private val mainScope = MainScope()
    private val utteranceProgressListeners = safeMutableListOf<TextToSpeech.UtteranceProgressListener>()

    private val utteranceIds = mutableListOf<String>()
    private val utteranceProgressDispatcher = object : UtteranceProgressDispatcher {

        override fun dispatchOnStart(utteranceId: String) {
            if (utteranceId !in utteranceIds) return
            utteranceProgressListeners.dispatchEach(Dispatchers.Main) {
                it.onStart(utteranceId)
            }
        }

        override fun dispatchOnEnd(utteranceId: String) {
            if (utteranceId !in utteranceIds) return
            utteranceProgressListeners.dispatchEach(Dispatchers.Main) {
                it.onEnd(utteranceId)
            }
        }

        override fun dispatchOnError(utteranceId: String, error: Error) {
            if (utteranceId !in utteranceIds) return
            utteranceProgressListeners.dispatchEach(Dispatchers.Main) {
                it.onError(utteranceId, error)
            }
        }
    }
    private var utteranceIdIndex = 0

    private val service by lazy { TextToSpeechService() }

    init {
        engine.addUtteranceProgressDispatcher(utteranceProgressDispatcher)
    }

    override fun speak(
        inputStream: TextToSpeechInputStream,
        params: Params,
        utteranceId: String?,
        strategy: TextToSpeech.Strategy
    ) {
        if (utteranceId != null && utteranceIds.contains(utteranceId)) {
            utteranceProgressDispatcher.dispatchOnError(
                utteranceId,
                Error.DuplicateUtteranceId
            )
            return
        }

        val nonNullUtteranceId = utteranceId ?: "$UTTERANCE_ID_PREFIX${utteranceIdIndex++}"
        val item = SpeechItem(inputStream, nonNullUtteranceId, params)
        val wrapper = SpeechItemWrapper(item, priority, engine)
        if (withGlobalQueue) {
            TextToSpeechManager.service.addSpeechItem(wrapper, strategy)
        } else {
            service.addSpeechItem(wrapper, strategy)
        }

        utteranceIds.add(nonNullUtteranceId)
    }

    override fun cancel(utteranceId: String) {
        mainScope.launch {
            utteranceIds.remove(utteranceId)
            if (withGlobalQueue) {
                TextToSpeechManager.service.removeSpeechItem(utteranceId, priority)
            } else {
                service.removeSpeechItem(utteranceId, priority)
            }
        }
    }

    override fun cancelAll() {
        if (utteranceIds.isEmpty()) return
        mainScope.launch {
            if (withGlobalQueue) {
                TextToSpeechManager.service.removeSpeechItems(utteranceIds, priority)
            } else {
                service.removeSpeechItems(utteranceIds, priority)
            }
            utteranceIds.clear()
        }
    }

    override fun addUtteranceProgressListener(listener: TextToSpeech.UtteranceProgressListener) {
        if (utteranceProgressListeners.contains(listener)) {
            return
        }
        utteranceProgressListeners.add(listener)
    }

    override fun removeUtteranceProgressListener(listener: TextToSpeech.UtteranceProgressListener) {
        utteranceProgressListeners.remove(listener)
    }

    override fun release() {
        utteranceProgressListeners.clear()
        engine.removeUtteranceProgressDispatcher(utteranceProgressDispatcher)
        mainScope.cancel()
    }
}