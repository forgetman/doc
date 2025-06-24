@file:Suppress("DEPRECATION")

package compat.network.api.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import compat.ext.wifi
import compat.network.def.WifiState
import compat.network.def.listener.wifi.WifiConnectStateListener
import compat.network.def.listener.wifi.WifiInfoListener
import compat.network.def.listener.wifi.WifiRSSIListener
import compat.network.def.listener.wifi.WifiScanResultListener
import compat.network.def.listener.wifi.WifiStateListener
import compat.network.def.listener.wifi.WifiSupplicantStateChangeListener
import compat.network.model.wifi.WifiConfigurationCompat

/**
 * @author yuansui
 * @since 2022/8/3
 */
internal interface Api {
    fun isEnabled(context: Context): Boolean {
        return context.wifi().isWifiEnabled
    }

    fun enable(context: Context): Boolean
    fun disable(context: Context): Boolean

    fun calculateSignalLevel(context: Context, rssi: Int, numLevels: Int): Int

    /**
     * 主动扫描新增限制: (8.0 & 8.1)
     * 每个后台应用：1次/30分钟
     *
     * 主动扫描新增限制: (9.0 & later)
     * 每个前台应用：4次/2分钟
     * 所有后台应用加起来：1次/30分钟
     *
     * 官方deprecate说明:
     * Deprecated: The ability for apps to trigger scan requests will be removed in a future release.
     * Deprecated in API level 28
     */
    fun startScan(context: Context): Boolean {
        if (!isEnabled(context)) return false
        @Suppress("DEPRECATION")
        return context.wifi().startScan()
    }

    @SuppressLint("MissingPermission")
    fun getScanResults(context: Context): List<ScanResult> {
        return context.wifi().scanResults
    }

    fun getConnectionInfo(context: Context, listener: WifiInfoListener)

    fun registerScanResultListener(context: Context, listener: WifiScanResultListener)
    fun unregisterScanResultListener(context: Context, listener: WifiScanResultListener)

    fun registerRSSIListener(context: Context, listener: WifiRSSIListener)
    fun unregisterRSSIListener(context: Context, listener: WifiRSSIListener)

    fun registerStateListener(context: Context, listener: WifiStateListener)
    fun unregisterStateListener(context: Context, listener: WifiStateListener)

    fun registerConnectListener(context: Context, listener: WifiConnectStateListener)
    fun unregisterConnectListener(context: Context, listener: WifiConnectStateListener)

    fun registerSupplicantStateListener(context: Context, listener: WifiSupplicantStateChangeListener)
    fun unregisterSupplicantStateListener(context: Context, listener: WifiSupplicantStateChangeListener)

    fun convertState(state: Int): WifiState {
        return when (state) {
            WifiManager.WIFI_STATE_ENABLED -> WifiState.ENABLED
            WifiManager.WIFI_STATE_DISABLED -> WifiState.DISABLED
            WifiManager.WIFI_STATE_ENABLING, WifiManager.WIFI_STATE_DISABLING -> WifiState.IDLE
            else -> WifiState.ERROR
        }
    }

    fun enableNetwork(context: Context, networkId: Int, attemptConnect: Boolean): Boolean
    fun disableNetwork(context: Context, networkId: Int): Boolean

    fun addNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int
    fun removeNetwork(context: Context, networkId: Int): Boolean
    fun removeNetwork(context: Context, ssid: String): Boolean
    fun updateNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int

    fun saveConfiguration(context: Context): Boolean
    fun getConfiguration(context: Context, ssid: String?): WifiConfigurationCompat?
    fun deleteConfiguration(context: Context, ssid: String?): Boolean

    fun disconnect(context: Context): Boolean
    fun reconnect(context: Context): Boolean

    fun getConfiguredNetworks(context: Context): List<WifiConfigurationCompat>
}