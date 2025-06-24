package vector.os.lifecycle

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * 可以自定义event的生命周期owner
 * @author yuansui
 * @since 2019-12-04
 */
interface LifecycleEventOwner : LifecycleOwner {

    val dispatcher: LifecycleEventDispatcher

    fun onCreate() {}
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}
    fun onDestroy() {}

    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle
}

class LifecycleEventDispatcher(val owner: LifecycleEventOwner) {
    private val registry: LifecycleRegistry = LifecycleRegistry(owner)
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var lastDispatchRunnable: DispatchRunnable? = null

    private val innerObserver: LifecycleObserver = object : LifecycleEventObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> owner.onCreate()
                Lifecycle.Event.ON_START -> owner.onStart()
                Lifecycle.Event.ON_RESUME -> owner.onResume()
                Lifecycle.Event.ON_PAUSE -> owner.onPause()
                Lifecycle.Event.ON_STOP -> owner.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    owner.onDestroy()
                    registry.removeObserver(this)
                }

                Lifecycle.Event.ON_ANY -> {
                    // do nothing
                }
            }
        }
    }

    /**
     * @return [Lifecycle] for the given [LifecycleOwner]
     */
    val lifecycle: Lifecycle
        get() = registry

    init {
        registry.addObserver(innerObserver)
    }

    private fun postRunnable(event: Lifecycle.Event) {
        lastDispatchRunnable?.run()

        val runnable = DispatchRunnable(registry, event)
        handler.postAtFrontOfQueue(runnable)
        lastDispatchRunnable = runnable
    }

    fun postOnCreate() {
        postRunnable(Lifecycle.Event.ON_CREATE)
    }

    fun postOnStart() {
        postRunnable(Lifecycle.Event.ON_START)
    }

    fun postOnResume() {
        postRunnable(Lifecycle.Event.ON_RESUME)
    }

    fun postOnPause() {
        postRunnable(Lifecycle.Event.ON_PAUSE)
    }

    fun postOnStop() {
        postRunnable(Lifecycle.Event.ON_STOP)
    }

    fun postOnDestroy() {
        postRunnable(Lifecycle.Event.ON_STOP)
        postRunnable(Lifecycle.Event.ON_DESTROY)
    }

    internal class DispatchRunnable(private val r: LifecycleRegistry, val event: Lifecycle.Event) :
        Runnable {
        private var wasExecuted = false

        override fun run() {
            if (!wasExecuted) {
                r.handleLifecycleEvent(event)
                wasExecuted = true
            }
        }
    }
}