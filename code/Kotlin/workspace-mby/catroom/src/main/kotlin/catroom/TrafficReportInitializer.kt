package catroom

import android.content.Context
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import androidx.work.Worker
import androidx.work.WorkerParameters
import catroom.manager.TrafficManager
import catroom.network.api.RoomApi
import catroom.network.createApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import vector.util.DeviceIdUtil
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TrafficReportInitializer : Initializer<Unit> {

    companion object {
        private const val LOG_TAG = "TrafficReportInitialize"
        private const val UNIQUE_WORK_NAME = "traffic_report"
    }

    override fun create(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReportTrafficWorker>(60, TimeUnit.MINUTES)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .addTag(LOG_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>?>> {
        return listOf(WorkManagerInitializer::class.java)
    }

    class ReportTrafficWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
        override fun doWork(): Result {
            try {
                runBlocking {
                    suspendCancellableCoroutine { cont ->
                        createApi<RoomApi>().reportTraffic(
                            DeviceIdUtil.id,
                            TrafficManager.diffRxBytes.toString(),
                            TrafficManager.diffTxBytes.toString()
                        ).flowOn(Dispatchers.IO).onEach {
                            L.d(LOG_TAG, "reportTraffic, success")
                            cont.resume(Unit)
                        }.catch { e ->
                            cont.resumeWithException(e)
                        }.launchIn(this)
                    }
                }
                return Result.success()
            } catch (e: Exception) {
                L.e(LOG_TAG, "doWork", e)
                return Result.retry()
            }
        }
    }
}