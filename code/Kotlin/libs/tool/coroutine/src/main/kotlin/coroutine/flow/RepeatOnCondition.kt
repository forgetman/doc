package coroutine.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

interface RepeatOption {
    interface Callback {
        fun onMet()
        fun onNotMet()
        fun onCancel()
    }

    fun met()
    fun notMet()
    fun cancel()
    fun setCallback(callback: Callback?)
}

fun RepeatOption(): RepeatOption = RepeatOpImpl()

private class RepeatOpImpl : RepeatOption {
    private var callback: RepeatOption.Callback? = null
    private var met = false

    override fun met() {
        met = true
        callback?.onMet()
    }

    override fun notMet() {
        met = false
        callback?.onNotMet()
    }

    override fun cancel() {
        callback?.onCancel()
    }

    override fun setCallback(callback: RepeatOption.Callback?) {
        this.callback = callback
        if (callback != null) {
            // 加入的时候主动回调第一次状态(模拟sticky效果)
            if (met) {
                callback.onMet()
            } else {
                callback.onNotMet()
            }
        }
    }
}

fun <T> Flow<T>.repeatOn(op: RepeatOption, context: CoroutineContext = EmptyCoroutineContext): Flow<T> = callbackFlow {
    op.repeatOnCallback(context) {
        this@repeatOn.collect {
            send(it)
        }
    }
    close()
}

private suspend fun RepeatOption.repeatOnCallback(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    // This scope is required to preserve context
    coroutineScope {
        // Instance of the running repeating coroutine
        var launchedJob: Job? = null

        var callback: RepeatOption.Callback? = null
        try {
            suspendCancellableCoroutine<Unit> { cont ->
                val mutex = Mutex()
                callback = object : RepeatOption.Callback {
                    override fun onMet() {
                        launchedJob = this@coroutineScope.launch {
                            // Mutex makes invocations run serially,
                            // coroutineScope ensures all child coroutines finish
                            mutex.withLock {
                                coroutineScope {
                                    withContext(context) {
                                        block()
                                    }
                                }
                            }
                        }
                    }

                    override fun onNotMet() {
                        this@coroutineScope.launch(context) {
                            launchedJob?.cancel()
                            launchedJob = null
                        }
                    }

                    override fun onCancel() {
                        cont.resume(Unit)
                    }
                }
                this@repeatOnCallback.setCallback(callback)
            }
        } finally {
            launchedJob?.cancel()
            callback?.let {
                this@repeatOnCallback.setCallback(null)
            }
        }
    }
}