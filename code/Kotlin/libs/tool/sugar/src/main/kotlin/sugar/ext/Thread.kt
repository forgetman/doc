package sugar.ext

import android.os.Looper
import android.view.View
import androidx.annotation.IntRange
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun isSubThread() = !isMainThread()

private typealias CoroutineCallback = suspend CoroutineScope.() -> Unit

@OptIn(DelicateCoroutinesApi::class)
private fun runOnTargetThread(
    context: CoroutineContext,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return (scope ?: GlobalScope).launch(context) {
        if (isActive) callback()
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun runOnTargetThread(
    context: CoroutineContext,
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return (scope ?: GlobalScope).launch(context) {
        val millis = timeUnit.toMillis(delay)
        delay(millis)
        if (isActive) callback()
    }
}

// <editor-fold defaultstate = expanded" desc = "runOnMainThread">
/**
 * 切换主线程
 */
fun runOnMainThread(owner: LifecycleOwner? = null, callback: CoroutineCallback): Job {
    return runOnTargetThread(Dispatchers.Main, owner?.lifecycleScope, callback)
}

fun runOnMainThread(scope: CoroutineScope?, callback: CoroutineCallback): Job {
    return runOnTargetThread(Dispatchers.Main, scope, callback)
}

fun runOnMainThread(view: View, callback: CoroutineCallback): Job {
    val job = runOnMainThread(scope = null, callback = callback)
    view.bindJob(job)
    return job
}

/**
 * 切换主线程
 * @param delay 延迟
 * @param timeUnit 时间单位
 */
fun runOnMainThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    owner: LifecycleOwner? = null,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main, delay, timeUnit, owner?.lifecycleScope, callback)
}

fun runOnMainThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main, delay, timeUnit, scope, callback)
}

fun runOnMainThread(delay: Long, timeUnit: TimeUnit, view: View, callback: CoroutineCallback): Job {
    val job = runOnMainThread(delay, timeUnit, callback = callback)
    view.bindJob(job)
    return job
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "runOnSubThread">
/**
 * 切换子线程
 */
fun runOnSubThread(owner: LifecycleOwner? = null, callback: CoroutineCallback): Job {
    return runOnTargetThread(Dispatchers.Default, owner?.lifecycleScope, callback)
}

fun runOnSubThread(scope: CoroutineScope?, callback: CoroutineCallback): Job {
    return runOnTargetThread(Dispatchers.Default, scope, callback)
}

fun runOnSubThread(view: View, callback: CoroutineCallback): Job {
    val job = runOnSubThread(scope = null, callback = callback)
    view.bindJob(job)
    return job
}

/**
 * 切换子线程
 * @param delay 延迟
 * @param timeUnit 时间单位
 */
fun runOnSubThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    owner: LifecycleOwner? = null,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Default, delay, timeUnit, owner?.lifecycleScope, callback)
}

fun runOnSubThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Default, delay, timeUnit, scope, callback)
}

fun runOnSubThread(delay: Long, timeUnit: TimeUnit, view: View, callback: CoroutineCallback): Job {
    val job = runOnSubThread(delay, timeUnit, callback = callback)
    view.bindJob(job)
    return job
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "runOnCurrThread">
fun runOnCurrThread(owner: LifecycleOwner? = null, callback: CoroutineCallback): Job {
    return if (isMainThread()) {
        runOnMainThread(owner, callback)
    } else {
        runOnSubThread(owner, callback)
    }
}

fun runOnCurrThread(scope: CoroutineScope?, callback: CoroutineCallback): Job {
    return if (isMainThread()) {
        runOnMainThread(scope, callback)
    } else {
        runOnSubThread(scope, callback)
    }
}

/**
 * 在当前线程运行, 不做任何线程切换
 */
fun runOnCurrThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    owner: LifecycleOwner? = null,
    callback: CoroutineCallback
): Job {
    return if (isMainThread()) {
        runOnMainThread(delay, timeUnit, owner, callback)
    } else {
        runOnSubThread(delay, timeUnit, owner, callback)
    }
}

fun runOnCurrThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return if (isMainThread()) {
        runOnMainThread(delay, timeUnit, scope, callback)
    } else {
        runOnSubThread(delay, timeUnit, scope, callback)
    }
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "ensureRunOnMainThread">
/**
 * 保证在主线程运行
 * 如果当前已经是在主线程, 不会进行额外的线程调度
 */
fun ensureRunOnMainThread(
    owner: LifecycleOwner? = null,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main.immediate, owner?.lifecycleScope, callback)
}

fun ensureRunOnMainThread(
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main.immediate, scope, callback)
}

fun ensureRunOnMainThread(view: View, callback: CoroutineCallback): Job {
    val job = ensureRunOnMainThread(scope = null, callback = callback)
    view.bindJob(job)
    return job
}

/**
 * 保证在主线程运行
 */
fun ensureRunOnMainThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    owner: LifecycleOwner? = null,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main.immediate, delay, timeUnit, owner?.lifecycleScope, callback)
}

fun ensureRunOnMainThread(
    @IntRange(from = 0) delay: Long,
    timeUnit: TimeUnit,
    scope: CoroutineScope?,
    callback: CoroutineCallback
): Job {
    return runOnTargetThread(Dispatchers.Main.immediate, delay, timeUnit, scope, callback)
}

fun ensureRunOnMainThread(delay: Long, timeUnit: TimeUnit, view: View, callback: CoroutineCallback): Job {
    val job = ensureRunOnMainThread(delay, timeUnit, callback = callback)
    view.bindJob(job)
    return job
}
// </editor-fold>

private fun View.bindJob(job: Job) {
    val listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            // do nothing
        }

        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            job.cancel()
        }
    }
    addOnAttachStateChangeListener(listener)
    job.invokeOnCompletion {
        removeOnAttachStateChangeListener(listener)
    }
}