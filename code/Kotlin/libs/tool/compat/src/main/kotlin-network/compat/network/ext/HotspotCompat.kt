package compat.network.ext

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import compat.network.HotspotCompat
import compat.network.def.HotspotState
import compat.network.def.listener.HotspotStateListener
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.lifecycleOwner
import sugar.ext.observeDestroy
import sugar.ext.self


fun HotspotCompat.onStateChanged(
    context: Context,
    owner: LifecycleOwner? = null,
    callback: (state: HotspotState) -> Unit
) = self {
    val listener = HotspotStateListener { state -> callback(state) }
    addOnStateChangedListener(context, listener)
    val o = owner ?: context.lifecycleOwner
    o?.observeDestroy {
        removeOnStateChangedListener(context, listener)
    }
}

fun HotspotCompat.onStateChanged(
    context: Context,
    scope: CoroutineScope,
    callback: (state: HotspotState) -> Unit
) {
    val listener = HotspotStateListener { state -> callback(state) }
    addOnStateChangedListener(context, listener)
    scope.observeCancel {
        removeOnStateChangedListener(context, listener)
    }
}