@file:Suppress("DEPRECATION")

package compat.network.api

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import compat.ext.checkPermission
import compat.ext.connectivity
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import compat.network.ext.toConnState
import logger.L
import sugar.collection.mapListOf
import sugar.collection.safeMutableListOf
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2020/1/7
 */
@SuppressLint("MissingPermission") // 统一忽略 Manifest.permission.ACCESS_NETWORK_STATE
internal class ApiImpl : Api {

    companion object {
        private const val LOG_TAG = "NetworkCompat_ApiImpl"
    }

    private data class NetworkStateWrapper(
        val state: NetworkState,
        val network: Network
    )

    private var networkCallBack: ConnectivityManager.NetworkCallback? = null
    private val listeners = safeMutableListOf<NetworkListener>()

    // 权限检查的简化
    private fun hasNetworkStatePermission(context: Context): Boolean {
        return context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    }

    override fun isAvailable(context: Context): Boolean {
        if (!hasNetworkStatePermission(context)) return false
        val connectivityManager = context.connectivity()
        return connectivityManager.activeNetworkInfo?.isAvailable == true
    }

    override fun isConnected(context: Context): Boolean {
        if (!hasNetworkStatePermission(context)) return false
        val connectivityManager = context.connectivity()
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    override fun getActiveNetworkState(context: Context): NetworkState {
        if (!hasNetworkStatePermission(context)) return NetworkState.Idle
        val connectivityManager = context.connectivity()
        return mapNetworkTypeToState(connectivityManager.activeNetworkInfo?.type)
    }

    private fun mapNetworkTypeToState(type: Int?): NetworkState {
        return when (type) {
            ConnectivityManager.TYPE_WIFI -> NetworkState.Wifi(true)
            ConnectivityManager.TYPE_MOBILE -> NetworkState.Cellular(true)
            ConnectivityManager.TYPE_ETHERNET -> NetworkState.Ethernet(true)
            ConnectivityManager.TYPE_VPN -> NetworkState.Vpn(true)
            ConnectivityManager.TYPE_BLUETOOTH -> NetworkState.Bluetooth(true)
            else -> NetworkState.Idle
        }
    }

    override fun registerListener(context: Context, listener: NetworkListener) {
        if (!hasNetworkStatePermission(context)) return

        if (listeners.contains(listener)) return
        listeners.add(listener)

        if (networkCallBack != null) return

        val callback = createNetworkCallback()
        context.connectivity().registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        networkCallBack = callback
    }

    private fun createNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            private var lastState: NetworkState = NetworkState.Idle
            private val internetNetworkMap = mapListOf<String, NetworkStateWrapper>()

            override fun onAvailable(network: Network) {
                listeners.forEachElement { it.onAvailable(network) }
            }

            override fun onUnavailable() {
                listeners.forEachElement(NetworkListener::onUnavailable)
            }

            override fun onLost(network: Network) {
                L.d(LOG_TAG, "onLost, network: $network")
                internetNetworkMap.removeWhen { it.network.toString() == network.toString() }
                checkValidatedNetwork()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                L.d(LOG_TAG, "onCapabilitiesChanged, network: $network, networkCapabilities: $networkCapabilities")
                handleCapabilitiesChanged(network, networkCapabilities)
            }

            private fun handleCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    val state = networkCapabilities.toConnState()
                    val stateTag = state.tag()
                    internetNetworkMap.remove(stateTag)
                    internetNetworkMap.add(stateTag, NetworkStateWrapper(state, network))
                    checkValidatedNetwork()
                }

                listeners.forEachElement {
                    it.onCapabilitiesChanged(networkCapabilities)
                }
            }

            private fun notifyChanged(state: NetworkState) {
                if (lastState == state) return
                lastState = state
                listeners.forEachElement {
                    it.onConnectStateChanged(state)
                }
            }

            private fun checkValidatedNetwork() {
                sortNetworks()
                L.d(LOG_TAG, "checkValidNetwork, internetNetworkMap: $internetNetworkMap")
                val wrapper = internetNetworkMap.firstOrNull()
                notifyChanged(wrapper?.state ?: NetworkState.Idle)
            }

            /**
             * 排序网络, 以固定的优先级排列
             * 暂时先把蜂窝网络排在最后, 其他的顺序还需要经过测试才知道先后
             */
            private fun sortNetworks() {
                internetNetworkMap.sortBy {
                    when (it.state) {
                        is NetworkState.Cellular -> 1
                        else -> 0 // Cellular
                    }
                }
            }
        }
    }

    override fun unregisterListener(context: Context, listener: NetworkListener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            networkCallBack?.let {
                context.connectivity().unregisterNetworkCallback(it)
                networkCallBack = null
            }
        }
    }

    override fun bindProcessToNetwork(context: Context, network: Network?): Boolean {
        return ConnectivityManager.setProcessDefaultNetwork(network)
    }

    override fun getActiveNetwork(context: Context): Network? {
        // FIXME: 5.0暂时不支持
        return null
    }

    override fun isCaptivePortal(context: Context, network: Network?): Boolean {
        if (!hasNetworkStatePermission(context)) return false
        // TODO: AI代码, 暂时没有校验
        val manager = context.systemService<ConnectivityManager>()
        val networkInfo: NetworkInfo = manager.activeNetworkInfo ?: return false
        return networkInfo.extraInfo == "captive portal"
    }
}