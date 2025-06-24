@file:Suppress("DEPRECATION")

package compat.network.api.hotspot

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import compat.ext.wifi
import compat.network.def.HotspotState
import compat.network.def.listener.HotspotConnectedDevicesListener
import compat.network.def.listener.HotspotStateListener
import compat.network.def.listener.ReceiverListener
import compat.network.ext.toCompatConfiguration
import compat.network.ext.toWifiConfiguration
import compat.network.model.hotspot.ConnectedDevice
import compat.network.model.wifi.WifiConfigurationCompat
import logger.L
import java.lang.reflect.Method


/**
 * @author yuansui
 * @since 2023/7/10
 */
internal class ApiImpl : Api {

    companion object {
        private const val LOG_TAG = "Hotspot_ApiImpl"
        private const val WIFI_AP_CONNECTION_CHANGED_ACTION = "android.net.wifi.WIFI_AP_CONNECTION_CHANGED_ACTION"
        private const val WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED"
        private const val EXTRA_STATE = "wifi_state"
    }

    private val stateListeners = ReceiverListener<HotspotStateListener>(
        WIFI_AP_STATE_CHANGED
    ) { _, intent ->
        // 10---正在关闭；11---已关闭；12---正在开启；13---已开启
        val state = intent.getIntExtra(EXTRA_STATE, 0) % 10
        L.d(LOG_TAG, "state = $state")
        when (state) {
            WifiManager.WIFI_STATE_ENABLING -> forEach { it.onStateChanged(HotspotState.ENABLING) }
            WifiManager.WIFI_STATE_ENABLED -> forEach { it.onStateChanged(HotspotState.ENABLED) }
            WifiManager.WIFI_STATE_DISABLING -> forEach { it.onStateChanged(HotspotState.DISABLING) }
            WifiManager.WIFI_STATE_DISABLED -> forEach { it.onStateChanged(HotspotState.DISABLED) }
        }
    }

    private val connectedDevicesListeners = ReceiverListener<HotspotConnectedDevicesListener>(
        WIFI_AP_CONNECTION_CHANGED_ACTION,
        WIFI_AP_STATE_CHANGED
    ) { context, intent ->
        when (intent.action) {
            WIFI_AP_CONNECTION_CHANGED_ACTION -> {
                forEach { it.onConnectedDevicesChanged(getConnectedDevices(context)) }
            }

            WIFI_AP_STATE_CHANGED -> {
                // 10---正在关闭；11---已关闭；12---正在开启；13---已开启
                val state = intent.getIntExtra(EXTRA_STATE, 0) % 10
                L.d(LOG_TAG, "state = $state")
                when (state) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        forEach { it.onConnectedDevicesChanged(getConnectedDevices(context)) }
                    }

                    WifiManager.WIFI_STATE_DISABLED -> {
                        forEach { it.onConnectedDevicesChanged(emptyList()) }
                    }
                }
            }
        }
    }

    override fun isEnabled(context: Context): Boolean {
        val wifiManager = context.wifi()
        try {
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "is wifi ap enabled", e)
        }
        return false
    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun enable(context: Context, configuration: WifiConfigurationCompat): Boolean {
        val wifiManager = context.wifi()
        try {
            val method = wifiManager.javaClass.getDeclaredMethod(
                "startSoftAp", WifiConfiguration::class.java
            )
            method.isAccessible = true
            return method.invoke(wifiManager, configuration.toWifiConfiguration()) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "enable wifi ap", e)
        }
        return false
    }

    override fun disable(context: Context): Boolean {
        val wifiManager = context.wifi()
        try {
            val method = wifiManager.javaClass.getDeclaredMethod("stopSoftAp")
            method.isAccessible = true
            return method.invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "disable wifi ap", e)
        }
        return false
    }

    override fun getConnectedDevices(context: Context): List<ConnectedDevice> {
        try {
            val wifiManager = context.wifi()
            //断连但不封锁该用户
            val method: Method = wifiManager.javaClass.getMethod("softApGetConnectedStationsDetail")
            method.isAccessible = true

            @Suppress("UNCHECKED_CAST") val details = method.invoke(wifiManager) as List<String>
            return details.map {
                val fields = it.split(" ")
                val deviceName = if (fields.size >= 3) fields[2] else ""
                val ipAddress = if (fields.size >= 2) fields[1] else ""
                val macAddress = if (fields.isNotEmpty()) fields[0] else ""
                ConnectedDevice(deviceName, ipAddress, macAddress)
            }
        } catch (e: Exception) {
            L.e(LOG_TAG, "getConnectedDevices", e)
            return emptyList()
        }
    }

    override fun addOnStateChangedListener(context: Context, listener: HotspotStateListener) {
        stateListeners.add(context, listener)
    }

    override fun removeOnStateChangedListener(context: Context, listener: HotspotStateListener) {
        stateListeners.remove(context, listener)
    }

    override fun addOnConnectedDevicesChangedListener(
        context: Context,
        listener: HotspotConnectedDevicesListener
    ) {
        connectedDevicesListeners.add(context, listener)
    }

    override fun removeOnConnectedDevicesChangedListener(
        context: Context,
        listener: HotspotConnectedDevicesListener
    ) {
        connectedDevicesListeners.remove(context, listener)
    }

    override fun setApConfiguration(context: Context, configuration: WifiConfigurationCompat): Boolean {
        val wifiManager = context.wifi()
        return try {
            val method = wifiManager.javaClass.getMethod(
                "setWifiApConfiguration", WifiConfiguration::class.java
            )
            method.invoke(wifiManager, configuration.toWifiConfiguration()) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "setApConfiguration", e)
            false
        }
    }

    override fun getApConfiguration(context: Context): WifiConfigurationCompat {
        val wifiManager = context.wifi()
        return try {
            val method = wifiManager.javaClass.getDeclaredMethod("getWifiApConfiguration")
            val config = method.invoke(wifiManager) as WifiConfiguration
            config.toCompatConfiguration()
        } catch (e: Exception) {
            L.e(LOG_TAG, "getApConfiguration", e)
            WifiConfigurationCompat.Builder().build()
        }
    }

    override fun blockDevice(context: Context, device: ConnectedDevice): Boolean {
        return try {
            val wifiManager = context.wifi()
            val method: Method = wifiManager.javaClass.getMethod("softApBlockStation", String::class.java)
            method.invoke(wifiManager, device.macAddress) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "blockDevice", e)
            false
        }
    }

    override fun unblockDevice(context: Context, macAddress: String): Boolean {
        return try {
            val wifiManager = context.wifi()
            wifiManager.javaClass.getMethod("softApUnblockStation", String::class.java)
                .invoke(wifiManager, macAddress) as Boolean
        } catch (e: Exception) {
            L.e(LOG_TAG, "unblockDevice", e)
            false
        }
    }
}