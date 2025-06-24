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
import catroom.service.CheckUpgradeService
import logger.L
import vector.ext.startServ
import java.util.concurrent.TimeUnit

class CheckUpgradeInitializer : Initializer<Unit> {

    companion object {
        private const val LOG_TAG = "CheckUpgradeInitializer"
        private const val UNIQUE_WORK_NAME = "check_upgrade"
    }

    override fun create(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReportTrafficWorker>(1, TimeUnit.DAYS)
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

    class ReportTrafficWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

        override fun doWork(): Result {
            L.d(LOG_TAG, "doWork, start check upgrade")
            applicationContext.startServ<CheckUpgradeService>()
            return Result.success()
        }
    }
}