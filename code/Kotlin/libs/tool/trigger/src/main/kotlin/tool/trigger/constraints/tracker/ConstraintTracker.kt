package tool.trigger.constraints.tracker

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import sugar.ext.runOnMainThread
import tool.trigger.constraints.controller.ConstraintListener

/**
 * @author yuansui
 * @since 2023/4/17
 */
internal abstract class ConstraintTracker<T>(context: Context) {
    protected val appContext: Context = context.applicationContext
    private val lock = Any()
    private val listeners = LinkedHashSet<ConstraintListener<T>>()

    private var currentState: T? = null
    private var mainScope: CoroutineScope? = null

    fun addListener(listener: ConstraintListener<T>) {
        synchronized(lock) {
            if (listeners.add(listener)) {
                if (listeners.size == 1) {
                    currentState = initialState
                    startTracking()
                    mainScope = MainScope()
                }
                @Suppress("UNCHECKED_CAST")
                listener.onConstraintChanged(currentState as T)
            }
        }
    }

    fun removeListener(listener: ConstraintListener<T>) {
        synchronized(lock) {
            if (listeners.remove(listener) && listeners.isEmpty()) {
                stopTracking()
                mainScope?.cancel()
                mainScope = null
            }
        }
    }

    var state: T
        get() {
            return currentState ?: initialState
        }
        set(newState) {
            synchronized(lock) {
                if (currentState != null && (currentState == newState)) {
                    return
                }
                currentState = newState

                // onConstraintChanged may lead to calls to addListener or removeListener.
                // This can potentially result in a modification to the set while it is being
                // iterated over, so we handle this by creating a copy and using that for
                // iteration.
                val listenersList = listeners.toList()
                runOnMainThread(mainScope) {
                    listenersList.forEach { listener ->
                        // currentState was initialized by now
                        @Suppress("UNCHECKED_CAST")
                        listener.onConstraintChanged(currentState as T)
                    }
                }
            }
        }

    /**
     * Determines the initial state of the constraint being tracked.
     */
    abstract val initialState: T

    /**
     * Start tracking for constraint state changes.
     */
    abstract fun startTracking()

    /**
     * Stop tracking for constraint state changes.
     */
    abstract fun stopTracking()

}