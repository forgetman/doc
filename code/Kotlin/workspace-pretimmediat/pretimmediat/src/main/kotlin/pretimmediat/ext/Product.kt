package pretimmediat.ext

import coroutine.flow.launchForever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.network.api.GlobalApi
import pretimmediat.network.createApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val LOG_TAG = "Product"

@Suppress("OPT_IN_USAGE")
fun <R> ensureUserIdFlow(
    userId: String?,
    appSsid: String?,
    transform: suspend (value: String) -> Flow<R>
): Flow<R> {
    if (userId.isNullOrEmpty()) {
        // 请求接口
        return callbackFlow {
            val info = suspendCancellableCoroutine { cont ->
                if (appSsid.isNullOrEmpty()) {
                    cont.resumeWithException(IllegalArgumentException("appSsid is null or empty"))
                } else {
                    createApi<GlobalApi>().copyInfo(appSsid).catch { e ->
                        L.e(LOG_TAG, "copyInfo", e)
                        cont.resumeWithException(e)
                    }.onEach {
                        val id = it.appUserId
                        if (id.isNullOrEmpty()) {
                            cont.resumeWithException(IllegalArgumentException("appUserId is null or empty"))
                        } else {
                            cont.resume(id)
                        }
                    }.launchForever()
                }
            }
            L.d(LOG_TAG, "copyInfo, newId = $info")
            trySend(info)
            close()
        }.flatMapConcat(transform)
    } else {
        return flow { emit(userId) }.flatMapConcat(transform)
    }
}