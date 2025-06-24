package compat.network.api

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.connectivity
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import compat.network.ext.toConnState
import logger.L
import sugar.collection.safeMutableListOf

@SuppressLint("MissingPermission") // 统一忽略 Manifest.permission.ACCESS_NETWORK_STATE
@RequiresApi(Build.VERSION_CODES.N)
class Api24Impl : Api by Api23Impl() {

    companion object {
        private const val LOG_TAG = "NetworkCompat_Api24Impl"
    }

    private var networkCallBack: ConnectivityManager.NetworkCallback? = null
    private val listeners = safeMutableListOf<NetworkListener>()
    private var lastState: NetworkState = NetworkState.Idle


    override fun registerListener(context: Context, listener: NetworkListener) {
        if (listeners.contains(listener)) return
        listeners.add(listener)
        if (networkCallBack != null) return
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                L.d(LOG_TAG, "onLost, network: $network")
                notifyChanged(NetworkState.Idle)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                L.d(LOG_TAG, "onCapabilitiesChanged, network: $network, networkCapabilities: $networkCapabilities")
                notifyChanged(networkCapabilities.toConnState())
            }
        }
        context.connectivity().registerDefaultNetworkCallback(callback)
        networkCallBack = callback
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

    fun notifyChanged(state: NetworkState) {
        if (lastState == state) return
        lastState = state
        listeners.forEachElement {
            it.onConnectStateChanged(state)
        }
    }
}