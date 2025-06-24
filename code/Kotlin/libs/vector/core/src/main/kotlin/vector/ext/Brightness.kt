package vector.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeAtLeast
import sugar.ext.observeDestroy
import vector.util.Brightness

fun Brightness.System.onChanged(
    owner: LifecycleOwner,
    requiredState: Lifecycle.State? = null,
    listener: Brightness.System.Listener
) {
    val l = createListenerByState(owner, requiredState, listener) ?: return
    registerListener(l)

    if (requiredState != null) {
        owner.observeAtLeast(requiredState) {
            listener.onBrightnessChanged(getPercent())
        }
    }

    owner.observeDestroy {
        unregisterListener(l)
    }
}

fun Brightness.System.onChanged(
    scope: CoroutineScope,
    listener: Brightness.System.Listener
) {
    registerListener(listener)
    scope.observeCancel {
        unregisterListener(listener)
    }
}

private fun createListenerByState(
    owner: LifecycleOwner,
    requiredState: Lifecycle.State?,
    listener: Brightness.System.Listener
): Brightness.System.Listener? {
    owner.lifecycle.apply {
        if (currentState == Lifecycle.State.DESTROYED) return null

        return object : Brightness.System.Listener {
            override fun onBrightnessChanged(percent: Int) {
                if (requiredState != null && !currentState.isAtLeast(requiredState)) {
                    return
                }
                listener.onBrightnessChanged(percent)
            }
        }
    }
}
