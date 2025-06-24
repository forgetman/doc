package vector.os.lifecycle

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class LifecycleDispatcher(provider: LifecycleOwner) {

    private val registry: LifecycleRegistry = LifecycleRegistry(provider)
    private val handler: Handler
    private var lastDispatchRunnable: DispatchRunnable? = null

    init {
        @Suppress("DEPRECATION")
        handler = Handler()
    }

    fun postDispatchRunnable(event: Lifecycle.Event) {
        lastDispatchRunnable?.run()
        lastDispatchRunnable = DispatchRunnable(registry, event)
        handler.postAtFrontOfQueue(lastDispatchRunnable!!)
    }

    /**
     * [Lifecycle] for the given [LifecycleOwner]
     */
    val lifecycle: Lifecycle
        get() = registry

    internal class DispatchRunnable(
        private val registry: LifecycleRegistry,
        val event: Lifecycle.Event
    ) : Runnable {
        private var wasExecuted = false

        override fun run() {
            if (!wasExecuted) {
                registry.handleLifecycleEvent(event)
                wasExecuted = true
            }
        }
    }
}