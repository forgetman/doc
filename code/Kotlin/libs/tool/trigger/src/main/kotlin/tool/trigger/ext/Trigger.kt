package tool.trigger.ext

import androidx.lifecycle.LifecycleOwner
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy
import tool.trigger.Trigger

fun Trigger.onTrigger(owner: LifecycleOwner, action: () -> Unit) {
    val listener = Trigger.Listener { action() }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
        reset()
    }
}

fun Trigger.onTrigger(scope: CoroutineScope, action: () -> Unit) {
    val listener = Trigger.Listener { action() }
    addListener(listener)
    scope.observeCancel {
        removeListener(listener)
        reset()
    }
}