package bluetooth.ext

import androidx.lifecycle.LifecycleOwner
import bluetooth.BluetoothStateHelper
import bluetooth.BluetoothStateHelper.Listener
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy

fun BluetoothStateHelper.onStateChanged(owner: LifecycleOwner, listener: Listener) {
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

fun BluetoothStateHelper.onStateChanged(scope: CoroutineScope, listener: Listener) {
    addListener(listener)
    scope.observeCancel {
        removeListener(listener)
    }
}