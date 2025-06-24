@file:Suppress("unused")

package compat.network.ext

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import compat.network.WifiCompat
import compat.network.def.listener.wifi.WifiConnectStateListener
import compat.network.def.listener.wifi.WifiRSSIListener
import compat.network.def.listener.wifi.WifiScanResultListener
import compat.network.def.listener.wifi.WifiStateListener
import compat.network.def.listener.wifi.WifiSupplicantStateChangeListener
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy
import sugar.ext.self

// <editor-fold defaultstate = expanded" desc = "LifecycleOwner">
fun WifiCompat.onStateChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: WifiStateListener
) = self {
    registerStateListener(context, listener)
    owner.observeDestroy {
        unregisterStateListener(context, listener)
    }
}

fun WifiCompat.onRSSIChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: WifiRSSIListener
) = self {
    registerRSSIListener(context, listener)
    owner.observeDestroy {
        unregisterRSSIListener(context, listener)
    }
}

fun WifiCompat.onScanResultChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: WifiScanResultListener
) = self {
    registerScanResultListener(context, listener)
    owner.observeDestroy {
        unregisterScanResultListener(context, listener)
    }
}

fun WifiCompat.onConnectStateChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: WifiConnectStateListener
) = self {
    registerConnectListener(context, listener)
    owner.observeDestroy {
        unregisterConnectListener(context, listener)
    }
}

fun WifiCompat.onSupplicantStateChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: WifiSupplicantStateChangeListener
) = self {
    registerSupplicantStateListener(context, listener)
    owner.observeDestroy {
        unregisterSupplicantStateListener(context, listener)
    }
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "CoroutineScope">
fun WifiCompat.onStateChanged(
    context: Context,
    scope: CoroutineScope,
    listener: WifiStateListener
) = self {
    registerStateListener(context, listener)
    scope.observeCancel {
        unregisterStateListener(context, listener)
    }
}

fun WifiCompat.onRSSIChanged(
    context: Context,
    scope: CoroutineScope,
    listener: WifiRSSIListener
) = self {
    registerRSSIListener(context, listener)
    scope.observeCancel {
        unregisterRSSIListener(context, listener)
    }
}

fun WifiCompat.onScanResultChanged(
    context: Context,
    scope: CoroutineScope,
    listener: WifiScanResultListener
) = self {
    registerScanResultListener(context, listener)
    scope.observeCancel {
        unregisterScanResultListener(context, listener)
    }
}

fun WifiCompat.onConnectStateChanged(
    context: Context,
    scope: CoroutineScope,
    listener: WifiConnectStateListener
) = self {
    registerConnectListener(context, listener)
    scope.observeCancel {
        unregisterConnectListener(context, listener)
    }
}

fun WifiCompat.onSupplicantStateChanged(
    context: Context,
    scope: CoroutineScope,
    listener: WifiSupplicantStateChangeListener
) = self {
    registerSupplicantStateListener(context, listener)
    scope.observeCancel {
        unregisterSupplicantStateListener(context, listener)
    }
}
// </editor-fold>