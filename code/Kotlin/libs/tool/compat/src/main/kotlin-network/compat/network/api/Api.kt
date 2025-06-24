package compat.network.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Network
import android.telephony.TelephonyManager
import compat.ext.checkPermission
import compat.network.def.CellularType
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.systemService

/**
 * 网络环境相关操作
 * @author yuansui
 * @since 2020/1/7
 */
internal interface Api {
    /**
     * 是否有可用网络(无法知道是否能连通外网)
     * [SdkInt.L_21] 开始永远为true, 系统只返回可用的网络
     */
    fun isAvailable(context: Context): Boolean

    /**
     * 网络是否能连通外网, 比如有可能连了wifi但是wifi无法访问外网
     */
    fun isConnected(context: Context): Boolean

    /**
     * 获取网络类型
     */
    fun getActiveNetworkState(context: Context): NetworkState

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun getCellularType(context: Context): CellularType {
        val manager = context.systemService<TelephonyManager>()

        val type = if (isSdkAtLeast(SdkInt.N_24)) {
            if (!context.checkPermission(Manifest.permission.READ_PHONE_STATE)
                && !context.checkPermission(Manifest.permission.READ_BASIC_PHONE_STATE)
            ) {
                return CellularType.T_IDLE // 没有权限, 无法获取
            }
            manager.dataNetworkType
        } else {
            if (!context.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                return CellularType.T_IDLE // 没有权限, 无法获取
            }
            manager.networkType
        }

        return when (type) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> {
                CellularType.T_2G
            }

            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_EHRPD -> {
                CellularType.T_3G
            }

            TelephonyManager.NETWORK_TYPE_LTE -> CellularType.T_4G
            TelephonyManager.NETWORK_TYPE_NR -> CellularType.T_5G
            else -> CellularType.T_IDLE
        }
    }

    fun registerListener(context: Context, listener: NetworkListener)
    fun unregisterListener(context: Context, listener: NetworkListener)

    fun bindProcessToNetwork(context: Context, network: Network?): Boolean

    fun getActiveNetwork(context: Context): Network?

    /**
     * 当前已经连接的网络(可能不是wifi)是否为强制门户: 需要认证
     */
    fun isCaptivePortal(context: Context, network: Network?): Boolean
}