/**
 * 采取拓展Context方式, 为了区分常规Context使用, 放到这个文件
 */
package vector.ext

import android.app.job.JobScheduler
import android.content.Context
import sugar.ext.systemService


fun Context?.cancelAllJobs() {
    this?.systemService<JobScheduler>()?.cancelAll()
}

fun Context?.cancelJob(jobId: Int) {
    this?.systemService<JobScheduler>()?.cancel(jobId)
}

fun Context?.cancelLastJob() {
    val scheduler = this?.systemService<JobScheduler>() ?: return
    val all = scheduler.allPendingJobs
    if (all.isNotEmpty()) {
        val lastJobId = all.last().id
        scheduler.cancel(lastJobId)
    }
}
