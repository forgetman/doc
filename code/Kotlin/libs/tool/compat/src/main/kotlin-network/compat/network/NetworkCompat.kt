@file:Suppress("unused")

package compat.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import compat.ext.checkPermission
import compat.network.api.Api
import compat.network.api.Api23Impl
import compat.network.api.Api24Impl
import compat.network.api.ApiImpl
import compat.network.def.CellularType
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.systemService


/**
 * 网络环境
 */
object NetworkCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.N_24) -> Api24Impl()
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    @SuppressLint("MissingPermission")
    fun isActiveNetworkMetered(context: Context): Boolean {
        if (!context.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) return false
        return context.systemService<ConnectivityManager>().isActiveNetworkMetered
    }

    fun isAvailable(context: Context): Boolean = api.isAvailable(context)

    fun isConnected(context: Context): Boolean = api.isConnected(context)

    fun getActiveNetworkState(context: Context): NetworkState =
        api.getActiveNetworkState(context)

    fun getCellularType(context: Context): CellularType = api.getCellularType(context)

    /**
     * 是否蜂窝电话
     */
    fun isCellular(context: Context): Boolean = getActiveNetworkState(context) is NetworkState.Cellular

    /**
     * 是否WIFI
     */
    fun isWifi(context: Context): Boolean = getActiveNetworkState(context) is NetworkState.Wifi

    fun registerListener(
        context: Context,
        listener: NetworkListener
    ) = api.registerListener(context, listener)

    fun unregisterListener(
        context: Context,
        listener: NetworkListener
    ) = api.unregisterListener(context, listener)

    fun bindProcessToNetwork(context: Context, network: Network?): Boolean =
        api.bindProcessToNetwork(context, network)

    fun getActiveNetwork(context: Context): Network? = api.getActiveNetwork(context)

    /**
     * 是否门户网页
     * @param network 需要查询的网络对象, 如果未空, 则查询当前活跃网络
     */
    fun isCaptivePortal(context: Context, network: Network?): Boolean =
        api.isCaptivePortal(context, network)
}