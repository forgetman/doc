package feature.tts.service

import androidx.annotation.MainThread
import feature.tts.TextToSpeech
import feature.tts.def.ServiceState
import feature.tts.def.SpeechOpCallback
import feature.tts.model.SpeechItemWrapper
import feature.tts.model.op.SpeechCancelOp
import feature.tts.model.op.SpeechOp
import feature.tts.model.op.SpeechPlayOp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import sugar.collection.SafeMutableList
import sugar.collection.safeMutableListOf
import sugar.ext.throwIfNull
import kotlin.coroutines.resume

internal class TextToSpeechService {

    companion object {
        private const val LOG_TAG = "TextToSpeechService"
    }

    val state = MutableStateFlow<ServiceState>(ServiceState.IDLE)

    private val queueMap = mutableMapOf<Int/*priority*/, SafeMutableList<SpeechItemWrapper>>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val threadDispatcher = Dispatchers.Default.limitedParallelism(1)
    private val mainScope = MainScope()

    internal fun addSpeechItem(wrapper: SpeechItemWrapper, strategy: TextToSpeech.Strategy) {
        val item = wrapper.base
        L.d(LOG_TAG, "addSpeechItem, id = ${item.utteranceId}, strategy = $strategy")

        val engine = wrapper.engine.throwIfNull("addSpeechItem, engine is null.")
        engine.preload(wrapper.base)

        fun addToQueue() {
            val queue = getSpeechQueue(wrapper.priority)
            when (strategy) {
                TextToSpeech.Strategy.FLUSH -> {
                    queue.clear()
                    queue.add(wrapper)
                }

                TextToSpeech.Strategy.INTERRUPT,
                TextToSpeech.Strategy.ADD_FIRST -> {
                    queue.add(0, wrapper)
                }

                TextToSpeech.Strategy.ADD -> {
                    queue.add(wrapper)
                }

                TextToSpeech.Strategy.FORCE -> queue.add(0, wrapper)
            }
        }

        when (val currState = state.value) {
            is ServiceState.IDLE -> {
                // 添加到队列后触发检查
                L.d(LOG_TAG, "addSpeechItem, add to queue, and check")
                addToQueue()
                checkQueues()
            }

            is ServiceState.PLAYING -> {
                L.d(LOG_TAG, "addSpeechItem, add to queue when playing.")
                val shouldStopPlay = when (strategy) {
                    TextToSpeech.Strategy.FLUSH, TextToSpeech.Strategy.INTERRUPT -> true
                    else -> false
                }

                if (shouldStopPlay) {
                    val playingWrapper = currState.wrapper
                    if (playingWrapper.priority >= wrapper.priority) {
                        L.d(LOG_TAG, "addSpeechItem, cancel playing item.")
                        executeOp(SpeechCancelOp(playingWrapper)) {
                            state.value = ServiceState.IDLE

                            getSpeechQueue(playingWrapper.priority).remove(playingWrapper)
                            checkQueues()
                        }
                    }
                }
                addToQueue()
            }

            is ServiceState.CANCELING -> {
                L.d(LOG_TAG, "addSpeechItem, add to queue when canceling.")
                addToQueue()
            }

            is ServiceState.PAUSED -> {
                L.d(LOG_TAG, "addSpeechItem, add to queue when paused.")
                if (strategy == TextToSpeech.Strategy.FORCE) {
                    addToQueue()
                    resume()
                } else {
                    addToQueue()
                }
            }
        }
    }

    private fun executeOp(op: SpeechOp, callback: SpeechOpCallback) {
        L.d(LOG_TAG, "executeOption, op = $op")
        when (op) {
            is SpeechPlayOp -> {
                state.value = ServiceState.PLAYING(op.wrapper)
            }

            is SpeechCancelOp -> {
                val currState = state.value
                if (currState is ServiceState.PLAYING && currState.wrapper == op.wrapper) {
                    // 只有针对当前正在播放的item操作才算state进入canceling状态
                    state.value = ServiceState.CANCELING
                }
            }

            else -> Unit
        }

        callbackFlow {
            val result = suspendCancellableCoroutine { cont ->
                op.run { result ->
                    cont.resume(result)
                }
            }
            send(result)
            close()
        }.flowOn(threadDispatcher).onEach {
            L.d(LOG_TAG, "executeOption, result = $it, op = $op")
            callback.onResult(it)
        }.flowOn(Dispatchers.Main).launchIn(mainScope)
    }

    private fun checkQueues() {
        val currState = state.value
        L.d(LOG_TAG, "checkQueues, currState = $currState")
        if (currState is ServiceState.PLAYING || currState is ServiceState.CANCELING) return

        if (queueMap.isEmpty()) {
            state.value = ServiceState.IDLE
            return
        }

        val validQueues = queueMap.filterNot { it.value.isEmpty() }
        // 获取validQueues中优先级最高的队列
        val topQueue = validQueues.maxByOrNull { it.key }?.value
        val nextItem = topQueue?.firstOrNull() ?: return
        L.d(LOG_TAG, "checkQueues, nextItem = $nextItem")
        executeOp(SpeechPlayOp(nextItem)) { result ->
            // play完成后继续检查
            state.value = ServiceState.IDLE

            topQueue.remove(nextItem)
            L.d(LOG_TAG, "checkQueues, afterRemove queue = $topQueue")

            if (result) {
                checkQueues()
            }
        }
    }

    private fun getSpeechQueue(priority: Int): SafeMutableList<SpeechItemWrapper> {
        return queueMap.getOrPut(priority) { safeMutableListOf() }
    }

    @MainThread
    internal fun removeSpeechItem(
        utteranceId: String?,
        priority: Int
    ) {
        L.d(LOG_TAG, "removeSpeechItem, id = $utteranceId")
        val queue = getSpeechQueue(priority)
        L.d(LOG_TAG, "removeSpeechItem, queue = $queue")
        val item = queue.find { it.base.utteranceId == utteranceId }
        if (item != null) {
            removeSpeechItems(listOf(item)) {
                queue.remove(item)
                checkQueues()
            }
        }
    }

    @MainThread
    internal fun removeSpeechItems(
        utteranceIds: List<String>,
        priority: Int
    ) {
        L.d(LOG_TAG, "removeSpeechItems, priority = $priority, ids = $utteranceIds")
        val queue = getSpeechQueue(priority)

        val filterWrappers = queue.filter { it.base.utteranceId in utteranceIds }
        removeSpeechItems(filterWrappers) {
            queue.removeAll(filterWrappers)
            checkQueues()
        }
    }

    /**
     * 取消所有speech
     */
    fun cancelAll() {
        L.d(LOG_TAG, "cancelAll")
        removeSpeechItems(queueMap.values.flatten()) {
            queueMap.clear()
            state.value = ServiceState.IDLE
        }
    }

    /**
     * 取消指定优先级的speech,
     */
    fun cancel(priority: Int) {
        L.d(LOG_TAG, "cancel, priority = $priority")
        removeSpeechItems(getSpeechQueue(priority)) {
            getSpeechQueue(priority).clear()
            // 暂时设计为不触发队列检查
//            checkQueues()
        }
    }

    /**
     * 取消比指定的priority低的speech
     */
    fun cancelLowerPriority(priority: Int) {
        L.d(LOG_TAG, "cancelLowerPriority, priority = $priority")
        val filterMap = queueMap.filterKeys { it < priority }
        removeSpeechItems(filterMap.values.flatten()) {
            filterMap.forEach { (_, wrappers) ->
                wrappers.clear()
                // 暂时设计为不触发队列检查
//                checkQueues()
            }
        }
    }

    fun pause() {
        L.d(LOG_TAG, "pause")
        when (val currState = state.value) {
            is ServiceState.PLAYING -> {
                val playingWrapper = currState.wrapper
                L.d(LOG_TAG, "pause, cancel playing item.")
                executeOp(SpeechCancelOp(playingWrapper)) { result ->
                    L.d(LOG_TAG, "pause, cancel result = $result")
                    state.value = ServiceState.PAUSED
                    getSpeechQueue(playingWrapper.priority).remove(playingWrapper)
                }
            }

            else -> state.value = ServiceState.PAUSED
        }
    }

    fun resume() {
        L.d(LOG_TAG, "resume")
        if (state.value is ServiceState.PAUSED) {
            state.value = ServiceState.IDLE
            checkQueues()
        }
    }

    private fun removeSpeechItems(wrappers: List<SpeechItemWrapper>, callback: () -> Unit) {
        if (wrappers.isEmpty()) {
            callback()
            return
        }

        callbackFlow {
            var size = 0
            suspendCancellableCoroutine { cont ->
                wrappers.forEach { item ->
                    executeOp(SpeechCancelOp(item)) { result ->
                        if (result) {
                            size++
                        }
                        if (size >= wrappers.size) {
                            cont.resume(Unit)
                        }
                    }
                }
            }
            send(Unit)
            close()
        }.flowOn(threadDispatcher).onEach {
            callback()
        }.flowOn(Dispatchers.Main).launchIn(mainScope)
    }
}