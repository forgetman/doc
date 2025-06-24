package pretimmediat.stats

import android.app.Application
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.property.Properties
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Firebase统计
 */
class FirebaseStats : StatsOption {
    companion object {
        private const val LOG_TAG = "FirebaseStats"
    }

    private lateinit var context: Context

    override fun init(app: Application) {
        context = app

        callbackFlow {
            val id = suspendCancellableCoroutine { cont ->
                FirebaseAnalytics.getInstance(app).appInstanceId.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = task.result
                        cont.resume(id)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("unknown error"))
                    }
                }
            }
            trySend(id)
            close()
        }.flowOn(Dispatchers.Default).filterNotNull().catch { e ->
            L.e(LOG_TAG, "fetch appInstanceId", e)
        }.onEach { id ->
            Properties.appInstanceId.put(id)
        }.launchForever()
    }

    override fun onEvent(
        eventName: String,
        userId: String?,
        appSsid: String?,
        map: HashMap<String, String>?
    ) {
        val instance = FirebaseAnalytics.getInstance(context)
        if (!map.isNullOrEmpty()) {
            val bundle = Bundle()
            map.forEach { (key, value) ->
                bundle.putString(key, value)
            }
            instance.logEvent(eventName, bundle)
        } else {
            instance.logEvent(eventName, null)
        }
    }
}