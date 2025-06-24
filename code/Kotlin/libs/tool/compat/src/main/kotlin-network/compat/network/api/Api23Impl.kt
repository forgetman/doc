package compat.network.api

import android.annotation.SuppressLint
import android.content.Context
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.checkPermission
import compat.ext.connectivity
import compat.network.def.NetworkState
import compat.network.ext.toConnState


/**
 * @author yuansui
 * @since 2020/1/7
 */
@SuppressLint("MissingPermission") // 统一忽略 Manifest.permission.ACCESS_NETWORK_STATE
@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api by ApiImpl() {

    override fun isAvailable(context: Context): Boolean {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return false
        val manager = context.connectivity()
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_THREAD) -> true

            else -> false
        }
    }

    override fun isConnected(context: Context): Boolean {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return false
        val manager = context.connectivity()
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun getActiveNetworkState(context: Context): NetworkState {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return NetworkState.Idle
        val manager = context.connectivity()
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities.toConnState()
    }

    override fun bindProcessToNetwork(context: Context, network: Network?): Boolean {
        return context.connectivity().bindProcessToNetwork(network)
    }

    override fun getActiveNetwork(context: Context): Network? {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return null
        return context.connectivity().activeNetwork
    }

    override fun isCaptivePortal(context: Context, network: Network?): Boolean {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return false
        val manager = context.connectivity()
        val capabilities =
            manager.getNetworkCapabilities(network ?: getActiveNetwork(context)) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
    }
}