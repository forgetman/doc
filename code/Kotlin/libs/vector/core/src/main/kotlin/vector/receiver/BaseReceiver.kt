package vector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import compat.context.ContextCompat
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import sugar.ext.doOnNotNull

/**
 * 需要动态注册的广播接收器
 */
abstract class BaseReceiver<RECEIVER : BaseReceiver<RECEIVER>> : BroadcastReceiver() {

    abstract val filter: IntentFilter

    private var lifecycle: Lifecycle? = null
    private var lifecycleObserver: LifecycleEventObserver? = null

    private var scope: CoroutineScope? = null
    private var observeJob: Job? = null

    @Suppress("UNCHECKED_CAST")
    protected val self: RECEIVER
        get() = this as RECEIVER

    private var priority: Int? = null

    internal fun register(context: Context?): Boolean {
        if (context == null) return false

        ContextCompat.registerReceiver(context, this, filter.also { f ->
            priority?.let { p ->
                f.priority = p
            }
        })

        return true
    }

    internal fun register(context: Context?, owner: LifecycleOwner?): Boolean {
        if (!register(context)) return false

        owner?.lifecycle?.let { l ->
            if (l.currentState == Lifecycle.State.DESTROYED) {
                // ignore
                unregister(context)
                return false
            }

            val observer = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.lifecycle.removeObserver(this)
                        unregister(context)
                    }
                }
            }
            l.addObserver(observer)

            lifecycleObserver = observer
            lifecycle = l
        }

        return true
    }

    internal fun unregister(context: Context?): Boolean {
        if (context == null) return false
        context.unregisterReceiver(this@BaseReceiver)

        doOnNotNull(lifecycle, lifecycleObserver) { l, observer ->
            l.removeObserver(observer)
        }

        lifecycleObserver = null
        lifecycle = null

        scope = null
        observeJob?.cancel()
        observeJob = null

        return true
    }

    internal fun register(context: Context?, scope: CoroutineScope?): Boolean {
        if (!register(context)) return false

        if (scope != null) {
            if (!scope.isActive) {
                // ignore
                unregister(context)
                return false
            }

            observeJob = scope.observeCancel {
                unregister(context)
            }
            this.scope = scope
        }

        return true
    }

    protected fun isDestroyed(): Boolean {
        val l = lifecycle
        val s = scope
        return when {
            l != null -> {
                l.currentState == Lifecycle.State.DESTROYED
            }

            s != null -> {
                s.isActive
            }

            else -> false
        }
    }

    fun setPriority(priority: Int): RECEIVER {
        this.priority = priority
        return self
    }
}