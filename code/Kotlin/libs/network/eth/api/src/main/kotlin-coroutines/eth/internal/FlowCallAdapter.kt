package eth.internal

import eth.Task
import eth.def.Result
import eth.ext.asTaskFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * @author yuansui
 * @since 2020-08-16
 */
internal class FlowCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Flow<*>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(task: Task<R>): Flow<*> {
        @Suppress("EXPERIMENTAL_API_USAGE")
        return callbackFlow {
            val value = suspendCancellableCoroutine<R> { continuation ->
                continuation.invokeOnCancellation {
                    task.cancel()
                }

                val result: Result<R> = task.execute()
                if (result.isSuccessful) {
                    val data = result.data
                    if (data == null) {
                        val throwable: Throwable = NullPointerException("data is null")
                        continuation.resumeWithException(throwable)
                    } else {
                        continuation.resume(data)
                    }
                } else {
                    continuation.resumeWithException(result.exception)
                }
            }
            send(value)
            close()
        }.asTaskFlow(task)
    }
}