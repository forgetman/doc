package compat.network.api.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import androidx.annotation.RequiresApi
import compat.network.def.listener.wifi.WifiInfoListener
import sugar.ext.systemService

/**
 * PS: 如果[WifiInfo.getSSID]是[android.net.wifi.WifiManager.UNKNOWN_SSID], 是因为没有申请定位权限导致
 */
@SuppressLint("MissingPermission") // Manifest.permission.ACCESS_NETWORK_STATE
@RequiresApi(Build.VERSION_CODES.S)
internal class Api31Impl : Api by Api30Impl() {

    override fun getConnectionInfo(context: Context, listener: WifiInfoListener) {
        val manager = context.systemService<ConnectivityManager>()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val info = networkCapabilities.transportInfo as? WifiInfo? ?: return
                listener.onWifiInfo(info)
                manager.unregisterNetworkCallback(this)
            }
        }

        manager.registerNetworkCallback(request, callback)
    }
}
