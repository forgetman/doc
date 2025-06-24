package eth.ext

import eth.Task
import eth.model.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOn


internal class TaskFlow<T>(val task: Task<T>, private val callbackFlow: Flow<T>) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        callbackFlow.collect(collector)
    }
}

internal fun <T> Flow<T>.asTaskFlow(task: Task<T>): TaskFlow<T> {
    return TaskFlow(task, this)
}

interface ProgressFlow<T> : Flow<T> {
    fun onProgress(action: (Progress) -> Unit): Flow<T>
}

fun <T> Flow<T>.asProgressFlow(): ProgressFlow<T> {
    when (this) {
        is ProgressFlow -> return this
        is TaskFlow -> {
            val ioFlow = this.flowOn(Dispatchers.IO)
            val progressFlow = object : ProgressFlow<T> {
                override fun onProgress(action: (Progress) -> Unit): Flow<T> {
                    this@asProgressFlow.task.progressListener = Task.ProgressListener { progress ->
                        action(progress)
                    }
                    return ioFlow
                }

                override suspend fun collect(collector: FlowCollector<T>) {
                    ioFlow.collect(collector)
                }
            }
            return progressFlow
        }

        else -> throw IllegalArgumentException("Flow is not a TaskFlow or ProgressFlow")
    }
}