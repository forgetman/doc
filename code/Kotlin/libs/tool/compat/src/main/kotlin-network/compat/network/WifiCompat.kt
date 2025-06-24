package compat.network

import android.content.Context
import android.net.wifi.ScanResult
import compat.ext.wifi
import compat.network.api.wifi.Api
import compat.network.api.wifi.Api29Impl
import compat.network.api.wifi.Api30Impl
import compat.network.api.wifi.Api31Impl
import compat.network.api.wifi.Api33Impl
import compat.network.api.wifi.ApiImpl
import compat.network.def.WifiState
import compat.network.def.listener.wifi.WifiConnectStateListener
import compat.network.def.listener.wifi.WifiInfoListener
import compat.network.def.listener.wifi.WifiRSSIListener
import compat.network.def.listener.wifi.WifiScanResultListener
import compat.network.def.listener.wifi.WifiStateListener
import compat.network.def.listener.wifi.WifiSupplicantStateChangeListener
import compat.network.model.wifi.WifiConfigurationCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2022/1/20
 */
@Suppress("unused")
object WifiCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        isSdkAtLeast(SdkInt.S_31) -> Api31Impl()
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        isSdkAtLeast(SdkInt.Q_29) -> Api29Impl()
        else -> ApiImpl()
    }

    fun getState(context: Context): WifiState = api.convertState(context.wifi().wifiState)

    fun isEnabled(context: Context): Boolean = api.isEnabled(context)

    fun enable(context: Context): Boolean = api.enable(context)

    fun calculateSignalLevel(context: Context, rssi: Int, numLevels: Int): Int =
        api.calculateSignalLevel(context, rssi, numLevels)

    fun disable(context: Context): Boolean = api.disable(context)

    fun startScan(context: Context): Boolean = api.startScan(context)

    fun getScanResults(context: Context): List<ScanResult> = api.getScanResults(context)

    fun getConnectionInfo(context: Context, listener: WifiInfoListener) = api.getConnectionInfo(context, listener)

    fun registerStateListener(context: Context, listener: WifiStateListener) =
        api.registerStateListener(context, listener)

    fun unregisterStateListener(context: Context, listener: WifiStateListener) =
        api.unregisterStateListener(context, listener)

    fun registerRSSIListener(context: Context, listener: WifiRSSIListener) = api.registerRSSIListener(context, listener)

    fun unregisterRSSIListener(context: Context, listener: WifiRSSIListener) =
        api.unregisterRSSIListener(context, listener)

    fun registerScanResultListener(context: Context, listener: WifiScanResultListener) =
        api.registerScanResultListener(context, listener)

    fun unregisterScanResultListener(context: Context, listener: WifiScanResultListener) =
        api.unregisterScanResultListener(context, listener)

    fun registerConnectListener(context: Context, listener: WifiConnectStateListener) =
        api.registerConnectListener(context, listener)

    fun unregisterConnectListener(context: Context, listener: WifiConnectStateListener) =
        api.unregisterConnectListener(context, listener)

    fun registerSupplicantStateListener(context: Context, listener: WifiSupplicantStateChangeListener) =
        api.registerSupplicantStateListener(context, listener)

    fun unregisterSupplicantStateListener(context: Context, listener: WifiSupplicantStateChangeListener) =
        api.unregisterSupplicantStateListener(context, listener)

    fun enableNetwork(context: Context, netId: Int, attemptConnect: Boolean): Boolean =
        api.enableNetwork(context, netId, attemptConnect)

    fun disableNetwork(context: Context, netId: Int): Boolean = api.disableNetwork(context, netId)

    fun addNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int = api.addNetwork(context, wifiConfig)

    fun removeNetwork(context: Context, netId: Int): Boolean = api.removeNetwork(context, netId)

    fun updateNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int =
        api.updateNetwork(context, wifiConfig)

    fun saveConfiguration(context: Context): Boolean = api.saveConfiguration(context)

    fun getConfiguration(context: Context, ssid: String?): WifiConfigurationCompat? =
        api.getConfiguration(context, ssid)

    fun deleteConfiguration(context: Context, ssid: String?): Boolean = api.deleteConfiguration(context, ssid)

    fun disconnect(context: Context): Boolean = api.disconnect(context)

    fun reconnect(context: Context): Boolean = api.reconnect(context)

    fun getConfiguredNetworks(context: Context): List<WifiConfigurationCompat> = api.getConfiguredNetworks(context)
}