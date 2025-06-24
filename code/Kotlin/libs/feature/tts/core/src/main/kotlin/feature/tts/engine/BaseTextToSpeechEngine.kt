package feature.tts.engine

import android.content.Context
import android.os.SystemClock
import androidx.annotation.WorkerThread
import feature.tts.def.Error
import feature.tts.def.SpeechOpCallback
import feature.tts.def.UtteranceProgressDispatcher
import feature.tts.model.SpeechItem
import kotlinx.coroutines.Job
import logger.L
import sugar.collection.safeMutableListOf
import sugar.ext.runOnMainThread
import java.util.concurrent.TimeUnit

/**
 * 初始化机制设计
 * 1. 使用的时候, 如果没有初始化, 则初始化
 * 2. 如果没有需要播放的item, 60秒后取消初始化
 */
abstract class BaseTextToSpeechEngine(@Suppress("unused") protected val context: Context) : TextToSpeechEngine {

    companion object {
        private const val LOG_TAG = "TextToSpeechEngine"

        private const val DEINIT_DELAY = 60L
    }

    private val utteranceProgressDispatchers = safeMutableListOf<UtteranceProgressDispatcher>()

    val tag: String = this::class.java.simpleName + "-" + SystemClock.elapsedRealtime()

    var initialized: Boolean = false
        private set(value) {
            synchronized(this) {
                field = value
            }
        }

    private var deinitJob: Job? = null

    private val _holdItems = mutableListOf<SpeechItem>()
    val holdItems: List<SpeechItem>
        get() = _holdItems

    internal fun preload(item: SpeechItem) {
        addSpeechItem(item)
        onPreload(item)
    }

    @WorkerThread
    internal fun start(item: SpeechItem, callback: SpeechOpCallback) {
        if (!initialized) {
            // 没初始化成功, 不进行播放, 直接跳过
            L.d(LOG_TAG, "start, not initialized, id = ${item.utteranceId}")
            utteranceProgressDispatchers.forEachElement {
                it.dispatchOnError(
                    item.utteranceId,
                    Error.EngineNotInit
                )
            }
            callback.onResult(false)
            return
        }

        val result = item.play()
        if (!result) {
            callback.onResult(false)
            return
        }

        L.d(LOG_TAG, "start, id = ${item.utteranceId}")
        utteranceProgressDispatchers.forEachElement {
            it.dispatchOnStart(item.utteranceId)
        }
        onStart(item, callback)
    }

    @WorkerThread
    internal fun cancel(item: SpeechItem, callback: SpeechOpCallback) {
        removeSpeechItem(item)

        val result = item.cancel()
        if (!result) return

        L.d(LOG_TAG, "cancel, id = ${item.utteranceId}")
        onCancel(item, callback)

        utteranceProgressDispatchers.forEachElement {
            it.dispatchOnError(
                item.utteranceId,
                Error.Cancel
            )
        }
    }

    fun release() {
        utteranceProgressDispatchers.clear()

        deinitJob?.cancel()
        deinitJob = null

        _holdItems.clear()

        if (initialized) {
            initialized = false
            onDeinit()
        }

        onDestroy()
    }

    internal fun addUtteranceProgressDispatcher(dispatcher: UtteranceProgressDispatcher) {
        utteranceProgressDispatchers.add(dispatcher)
    }

    internal fun removeUtteranceProgressDispatcher(dispatcher: UtteranceProgressDispatcher) {
        utteranceProgressDispatchers.remove(dispatcher)
    }

    private fun addSpeechItem(item: SpeechItem) {
        _holdItems.add(item)
        deinitJob?.cancel()
        if (!initialized) {
            initialized = true
            L.d(LOG_TAG, "after addSpeechItem, init engine.")
            val result = onInit()
            if (!result) initialized = false
        }
    }

    private fun removeSpeechItem(item: SpeechItem) {
        val result = _holdItems.remove(item)
        if (result) prepareToDeInit()
    }

    private fun prepareToDeInit() {
        if (_holdItems.isEmpty()) {
            L.d(LOG_TAG, "$tag, prepareToDeInit, delay $DEINIT_DELAY seconds.")
            deinitJob?.cancel()
            deinitJob = runOnMainThread(DEINIT_DELAY, TimeUnit.SECONDS) {
                L.d(LOG_TAG, "prepareToDeInit, deinit engine.")
                initialized = false
                onDeinit()
            }
        }
    }

    internal fun clear() {
        _holdItems.clear()
    }
}