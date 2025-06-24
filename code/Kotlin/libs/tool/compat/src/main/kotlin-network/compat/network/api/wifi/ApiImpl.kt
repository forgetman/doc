@file:Suppress("DEPRECATION")

package compat.network.api.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import compat.ext.checkPermission
import compat.ext.wifi
import compat.network.def.listener.ReceiverListener
import compat.network.def.listener.wifi.WifiConnectStateListener
import compat.network.def.listener.wifi.WifiInfoListener
import compat.network.def.listener.wifi.WifiRSSIListener
import compat.network.def.listener.wifi.WifiScanResultListener
import compat.network.def.listener.wifi.WifiStateListener
import compat.network.def.listener.wifi.WifiSupplicantStateChangeListener
import compat.network.ext.plusDoubleQuote
import compat.network.ext.toCompatConfiguration
import compat.network.ext.toWifiConfiguration
import compat.network.model.wifi.WifiConfigurationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*


/**
 * @author yuansui
 * @since 2022/8/1
 *
 * RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE])
 */
internal class ApiImpl : Api {

    private val scanResultJobs by lazy { hashMapOf<WifiScanResultListener, Job>() }
    private val scanReceivers by lazy { hashMapOf<WifiScanResultListener, BroadcastReceiver>() }
    private var isConnected = false


    private val rssiListener by lazy {
        ReceiverListener<WifiRSSIListener>(
            WifiManager.RSSI_CHANGED_ACTION
        ) { context, intent ->
            if (isEnabled(context).not()) return@ReceiverListener
            val rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0)
            if (isConnected) forEach { it.onRSSIChanged(rssi) }
        }
    }

    private val stateListener by lazy {
        ReceiverListener<WifiStateListener>(
            WifiManager.WIFI_STATE_CHANGED_ACTION
        ) { _, intent ->
            val state = intent.getIntExtra(
                WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED
            )
            val convertState = convertState(state)
            forEach { it.onStateChanged(convertState) }
        }
    }

    private val connectStateListener by lazy {
        ReceiverListener<WifiConnectStateListener>(
            WifiManager.NETWORK_STATE_CHANGED_ACTION
        ) { _, intent ->
            val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                ?: return@ReceiverListener
            isConnected = info.isConnected
            forEach { it.onConnectStateChanged(info.isConnected) }
        }
    }

    private val supplicantStateListener by lazy {
        ReceiverListener<WifiSupplicantStateChangeListener>(
            WifiManager.SUPPLICANT_STATE_CHANGED_ACTION
        ) { _, intent ->
            // WIFI连接请求状态发生改变
            // 身份密码验证流程
            val state = intent.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE)
                ?: return@ReceiverListener
            forEach { it.onSupplicantStateChanged(state) }
        }
    }


    override fun enable(context: Context): Boolean {
        val wifi = context.wifi()
        if (wifi.isWifiEnabled) return true // 已经打开
        return wifi.setWifiEnabled(true)
    }

    override fun disable(context: Context): Boolean {
        val wifi = context.wifi()
        if (!wifi.isWifiEnabled) return false // 已经关闭
        return wifi.setWifiEnabled(false)
    }

    override fun calculateSignalLevel(context: Context, rssi: Int, numLevels: Int): Int {
        return WifiManager.calculateSignalLevel(rssi, numLevels)
    }

    override fun getConnectionInfo(context: Context, listener: WifiInfoListener) {
        listener.onWifiInfo(context.wifi().connectionInfo)
    }

    override fun registerScanResultListener(context: Context, listener: WifiScanResultListener) {
        if (scanReceivers.containsKey(listener)) return

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                @Suppress("OPT_IN_USAGE") val job = flow {
                    // 有可能获取到的是旧的扫描结果, 暂时不判断[WifiManager.EXTRA_RESULTS_UPDATED]的结果
                    emit(getScanResults(context))
                }.flowOn(Dispatchers.IO).onEach {
                    listener.onScanResultChanged(it)
                    scanResultJobs.remove(listener)
                }.flowOn(Dispatchers.Main).launchIn(GlobalScope)

                scanResultJobs[listener] = job
            }
        }

        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        })

        scanReceivers[listener] = receiver

        // 如果不主动扫描的话, 需要用户在WIFI设置里手动刷新才能收到结果
        startScan(context)
    }

    override fun unregisterScanResultListener(context: Context, listener: WifiScanResultListener) {
        scanReceivers[listener]?.let { receiver ->
            context.unregisterReceiver(receiver)
            scanReceivers.remove(listener)
        }

        scanResultJobs[listener]?.let { job ->
            job.cancel()
            scanResultJobs.remove(listener)
        }
    }

    override fun registerRSSIListener(context: Context, listener: WifiRSSIListener) {
        rssiListener.add(context, listener)
    }

    override fun unregisterRSSIListener(context: Context, listener: WifiRSSIListener) {
        rssiListener.remove(context, listener)
    }

    override fun registerStateListener(context: Context, listener: WifiStateListener) {
        stateListener.add(context, listener)
    }

    override fun unregisterStateListener(context: Context, listener: WifiStateListener) {
        stateListener.remove(context, listener)
    }

    override fun registerConnectListener(context: Context, listener: WifiConnectStateListener) {
        connectStateListener.add(context, listener)
    }

    override fun unregisterConnectListener(context: Context, listener: WifiConnectStateListener) {
        connectStateListener.remove(context, listener)
    }

    override fun registerSupplicantStateListener(
        context: Context,
        listener: WifiSupplicantStateChangeListener
    ) {
        supplicantStateListener.add(context, listener)
    }

    override fun unregisterSupplicantStateListener(
        context: Context,
        listener: WifiSupplicantStateChangeListener
    ) {
        supplicantStateListener.remove(context, listener)
    }

    override fun enableNetwork(context: Context, networkId: Int, attemptConnect: Boolean): Boolean {
        return context.wifi().enableNetwork(networkId, attemptConnect)
    }

    override fun disableNetwork(context: Context, networkId: Int): Boolean {
        return context.wifi().disableNetwork(networkId)
    }

    override fun addNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int {
        return context.wifi().addNetwork(wifiConfig.toWifiConfiguration())
    }

    override fun removeNetwork(context: Context, networkId: Int): Boolean {
        return context.wifi().removeNetwork(networkId)
    }

    override fun removeNetwork(context: Context, ssid: String): Boolean {
        val networkId = getConfiguration(context, ssid)?.networkId ?: return false
        return context.wifi().removeNetwork(networkId)
    }

    override fun updateNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int {
        return context.wifi().updateNetwork(wifiConfig.toWifiConfiguration())
    }

    override fun saveConfiguration(context: Context): Boolean {
        return context.wifi().saveConfiguration()
    }

    override fun getConfiguration(context: Context, ssid: String?): WifiConfigurationCompat? {
        if (ssid == null) return null
        return getConfiguredNetworks(context).find {
            it.ssid == ssid || it.ssid == ssid.plusDoubleQuote()
        }
    }

    override fun deleteConfiguration(context: Context, ssid: String?): Boolean {
        val result = getConfiguration(context, ssid) ?: return false
        val netId = result.networkId
        val disableResult = disableNetwork(context, netId)
        val removeResult = removeNetwork(context, netId)
        val saveResult = saveConfiguration(context)
        return disableResult && removeResult && saveResult
    }

    override fun disconnect(context: Context): Boolean {
        return context.wifi().disconnect()
    }

    override fun reconnect(context: Context): Boolean {
        return context.wifi().reconnect()
    }

    @SuppressLint("MissingPermission")
    override fun getConfiguredNetworks(context: Context): List<WifiConfigurationCompat> {
        if (!context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return emptyList()
        if (!context.checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) return emptyList()
        return context.wifi().configuredNetworks?.map { it.toCompatConfiguration() } ?: emptyList()
    }
}