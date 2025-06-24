package compat.telephony.ext

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import compat.telephony.TelephonyCompat
import compat.telephony.def.listener.CallStateListener
import compat.telephony.def.listener.SignalStrengthListener
import compat.telephony.def.listener.SimStateListener
import compat.telephony.def.listener.TelephonyStateListener
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy

fun TelephonyCompat.onStateChanged(context: Context, owner: LifecycleOwner, listener: TelephonyStateListener) {
    registerListener(context, listener)
    owner.observeDestroy {
        unregisterListener(context, listener)
    }
}

fun TelephonyCompat.onStateChanged(context: Context, scope: CoroutineScope, listener: TelephonyStateListener) {
    registerListener(context, listener)
    scope.observeCancel {
        unregisterListener(context, listener)
    }
}

fun TelephonyCompat.onSimStateChanged(context: Context, owner: LifecycleOwner, listener: SimStateListener) {
    registerSimStateListener(context, listener)
    owner.observeDestroy {
        unregisterSimStateListener(context, listener)
    }
}

fun TelephonyCompat.onSimStateChanged(context: Context, scope: CoroutineScope, listener: SimStateListener) {
    registerSimStateListener(context, listener)
    scope.observeCancel {
        unregisterSimStateListener(context, listener)
    }
}

fun TelephonyCompat.onSignalStrengthChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: SignalStrengthListener
) {
    registerSignalStrengthsListener(context, listener)
    owner.observeDestroy {
        unregisterSignalStrengthsListener(context, listener)
    }
}

fun TelephonyCompat.onSignalStrengthChanged(
    context: Context,
    scope: CoroutineScope,
    listener: SignalStrengthListener
) {
    registerSignalStrengthsListener(context, listener)
    scope.observeCancel {
        unregisterSignalStrengthsListener(context, listener)
    }
}

fun TelephonyCompat.onCallStateChanged(
    context: Context,
    owner: LifecycleOwner,
    listener: CallStateListener
) {
    registerCallStateListener(context, listener)
    owner.observeDestroy {
        unregisterCallStateListener(context, listener)
    }
}

fun TelephonyCompat.onCallStateChanged(
    context: Context,
    scope: CoroutineScope,
    listener: CallStateListener
) {
    registerCallStateListener(context, listener)
    scope.observeCancel {
        unregisterCallStateListener(context, listener)
    }
}