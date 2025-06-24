package compat.network.ext

import android.content.Context
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LifecycleOwner
import compat.network.NetworkCompat
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.lifecycleOwner
import sugar.ext.observeDestroy
import sugar.ext.self

// <editor-fold defaultstate = expanded" desc = "LifecycleOwner">
/**
 * @see [NetworkListener.onAvailable]
 */
fun NetworkCompat.onAvailable(
    context: Context,
    owner: LifecycleOwner? = null,
    callback: (network: Network?) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onAvailable(network: Network?) {
            callback(network)
        }
    }
    withLifecycleOwner(context, owner, listener)
}

/**
 * @see [NetworkListener.onUnavailable]
 */
fun NetworkCompat.onUnavailable(
    context: Context,
    owner: LifecycleOwner? = null,
    callback: () -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onUnavailable() {
            callback()
        }
    }
    withLifecycleOwner(context, owner, listener)
}

/**
 * @see [NetworkListener.onConnectStateChanged]
 */
fun NetworkCompat.onConnectStateChanged(
    context: Context,
    owner: LifecycleOwner? = null,
    callback: (state: NetworkState) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onConnectStateChanged(state: NetworkState) {
            callback(state)
        }
    }
    withLifecycleOwner(context, owner, listener)
}

fun NetworkCompat.onCapabilitiesChanged(
    context: Context,
    owner: LifecycleOwner? = null,
    callback: (capabilities: NetworkCapabilities) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onCapabilitiesChanged(networkCapabilities: NetworkCapabilities) {
            callback(networkCapabilities)
        }
    }
    withLifecycleOwner(context, owner, listener)
}

private fun NetworkCompat.withLifecycleOwner(
    context: Context,
    owner: LifecycleOwner?,
    listener: NetworkListener
) = self {
    registerListener(context, listener)
    val o = owner ?: context.lifecycleOwner
    o?.observeDestroy {
        unregisterListener(context, listener)
    }
}
// </editor-fold>


// <editor-fold defaultstate = expanded" desc = "CoroutineScope">
fun NetworkCompat.onAvailable(
    context: Context,
    coroutineScope: CoroutineScope,
    callback: (network: Network?) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onAvailable(network: Network?) {
            callback(network)
        }
    }
    withCoroutineScope(context, coroutineScope, listener)
}

fun NetworkCompat.onUnavailable(
    context: Context,
    coroutineScope: CoroutineScope,
    callback: () -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onUnavailable() {
            callback()
        }
    }
    withCoroutineScope(context, coroutineScope, listener)
}

fun NetworkCompat.onConnectStateChanged(
    context: Context,
    coroutineScope: CoroutineScope,
    callback: (state: NetworkState) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onConnectStateChanged(state: NetworkState) {
            callback(state)
        }
    }
    withCoroutineScope(context, coroutineScope, listener)
}

fun NetworkCompat.onCapabilitiesChanged(
    context: Context,
    coroutineScope: CoroutineScope,
    callback: (capabilities: NetworkCapabilities) -> Unit
) = self {
    val listener = object : NetworkListener {
        override fun onCapabilitiesChanged(networkCapabilities: NetworkCapabilities) {
            callback(networkCapabilities)
        }
    }
    withCoroutineScope(context, coroutineScope, listener)
}

private fun NetworkCompat.withCoroutineScope(
    context: Context,
    coroutineScope: CoroutineScope,
    listener: NetworkListener
) = self {
    registerListener(context, listener)
    coroutineScope.observeCancel {
        unregisterListener(context, listener)
    }
}
// </editor-fold>

internal fun NetworkCapabilities?.toConnState(): NetworkState {
    if (this == null) return NetworkState.Idle
    val validated = if (isSdkAtLeast(SdkInt.M_23)) {
        this.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
        // 低版本无法判断是否验证通过, 默认为 true
        true
    }
    return when {
        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.Cellular(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.Wifi(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkState.Ethernet(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkState.Vpn(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> NetworkState.WifiAware(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkState.Bluetooth(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> NetworkState.Lowpan(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_USB) -> NetworkState.Usb(validated)
        hasTransport(NetworkCapabilities.TRANSPORT_THREAD) -> NetworkState.Thread(validated)
        else -> NetworkState.Idle
    }
}