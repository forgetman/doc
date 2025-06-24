package sugar.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

private typealias LifecycleObserveAction = (LifecycleOwner) -> Unit

fun Lifecycle.observeCreate(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_CREATE, observeOnce, action)
}

fun Lifecycle.observeStart(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_START, observeOnce, action)
}

fun Lifecycle.observeResume(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_RESUME, observeOnce, action)
}

fun Lifecycle.observePause(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_PAUSE, observeOnce, action)
}

fun Lifecycle.observeStop(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_STOP, observeOnce, action)
}

fun Lifecycle.observeDestroy(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    observeEvent(Lifecycle.Event.ON_DESTROY, observeOnce, action)
}

fun Lifecycle.observeAtLeast(
    requiredState: Lifecycle.State,
    observeOnce: Boolean = false,
    action: LifecycleObserveAction
) {
    val requiredEvent = Lifecycle.Event.upTo(requiredState) ?: return
    observeEvent(requiredEvent, observeOnce, action)
}

/**
 * @param observeOnce 是否只监听一次消息就去掉监听
 */
private fun Lifecycle.observeEvent(
    requiredEvent: Lifecycle.Event,
    observeOnce: Boolean,
    action: LifecycleObserveAction
) {
    if (currentState === Lifecycle.State.DESTROYED) return

    addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (!observeOnce && event == Lifecycle.Event.ON_DESTROY) {
                removeObserver(this)
            }

            if (requiredEvent == event) {
                if (observeOnce) removeObserver(this)
                action(source)
            }
        }
    })
}

fun LifecycleOwner.observeCreate(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observeCreate(observeOnce, action)
}

fun LifecycleOwner.observeStart(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observeStart(observeOnce, action)
}

fun LifecycleOwner.observeResume(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observeResume(observeOnce, action)
}

fun LifecycleOwner.observePause(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observePause(observeOnce, action)
}

fun LifecycleOwner.observeStop(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observeStop(observeOnce, action)
}

fun LifecycleOwner.observeDestroy(observeOnce: Boolean = false, action: LifecycleObserveAction) {
    lifecycle.observeDestroy(observeOnce, action)
}

fun LifecycleOwner.observeAtLeast(
    requiredState: Lifecycle.State,
    observeOnce: Boolean = false,
    action: LifecycleObserveAction
) {
    lifecycle.observeAtLeast(requiredState, observeOnce, action)
}

