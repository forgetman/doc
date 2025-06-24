package catroom

import android.content.Context
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import logger.L
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UploadLogInitializer : Initializer<Unit> {

    companion object {
        private const val LOG_TAG = "UploadLogInitializer"
        private const val UNIQUE_WORK_NAME = "upload_log"
    }

    override fun create(context: Context) {
        val request = PeriodicWorkRequestBuilder<UploadLogWorker>(30, TimeUnit.MINUTES)
            .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .addTag(LOG_TAG)
            .build()

        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>?>> {
        return listOf(WorkManagerInitializer::class.java)
    }

    class UploadLogWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

        override fun doWork(): Result {
            L.d(LOG_TAG, "doWork, start upload log")
            try {
                runBlocking {
                    withTimeout(15000) {
                        suspendCancellableCoroutine { cont ->
                            L.upload { result ->
                                if (result) {
                                    cont.resume(Unit)
                                } else {
                                    cont.resumeWithException(Exception("upload log failed"))
                                }
                            }
                        }
                    }
                }
                L.d(LOG_TAG, "doWork, upload log success")
                return Result.success()
            } catch (e: Exception) {
                L.e(LOG_TAG, "doWork", e)
                return Result.retry()
            }
        }
    }
}