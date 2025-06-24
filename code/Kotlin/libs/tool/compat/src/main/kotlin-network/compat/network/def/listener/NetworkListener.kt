package compat.network.def.listener

import android.net.Network
import android.net.NetworkCapabilities
import compat.network.def.NetworkState

interface NetworkListener {
    /**
     * 有可用网络
     * @see [compat.network.api.Api.isAvailable]
     */
    fun onAvailable(network: Network?) {}

    /**
     * 无可用网络
     * @see [compat.network.api.Api.isAvailable]
     */
    fun onUnavailable() {}

    /**
     * 是否能连通外网
     * @see [compat.network.api.Api.isConnected]
     */
    fun onConnectStateChanged(state: NetworkState) {}

    /**
     * 网络能力变化
     */
    fun onCapabilitiesChanged(networkCapabilities: NetworkCapabilities) {}
}