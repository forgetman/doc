package compat.network.api.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import compat.ext.checkPermission
import compat.ext.wifi
import compat.network.model.wifi.WifiConfigurationCompat

/**
 * RequiresPermission Manifest.permission.ACCESS_FINE_LOCATION
 *
 * https://developer.android.google.cn/reference/kotlin/android/net/wifi/WifiManager
 * 整体用法已经更改, 由于权限问题, 多数方法已经无效
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Api29Impl : Api by ApiImpl() {

    override fun enable(context: Context): Boolean {
        openWifiPanel(context)
        return false
    }

    override fun disable(context: Context): Boolean {
        openWifiPanel(context)
        return false
    }

    private fun openWifiPanel(context: Context) {
        val intent = Intent(Settings.Panel.ACTION_WIFI)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun saveConfiguration(context: Context): Boolean {
        /**
         * do nothing
         *
         * There is no need to call this method,
         * addNetwork(android.net.wifi.WifiConfiguration),
         * updateNetwork(android.net.wifi.WifiConfiguration)
         * and removeNetwork(int) already persist the configurations automatically.
         */
        return false
    }

    @SuppressLint("MissingPermission")
    override fun addNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int {
        if (!context.checkPermission(Manifest.permission.CHANGE_WIFI_STATE)) return -1
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(wifiConfig.ssid)

        if (!wifiConfig.preSharedKey.isNullOrEmpty()) {
            builder.setWpa2Passphrase(wifiConfig.preSharedKey)
        }
        return context.wifi().addNetworkSuggestions(listOf(builder.build()))
    }

    override fun removeNetwork(context: Context, networkId: Int): Boolean {
        // 由于无法获取配置列表, 所以不能从配置列表中根据networkId来查找ssid
        return false
    }

    @SuppressLint("MissingPermission")
    override fun removeNetwork(context: Context, ssid: String): Boolean {
        if (!context.checkPermission(Manifest.permission.CHANGE_WIFI_STATE)) return false
        val builder = WifiNetworkSuggestion.Builder().setSsid(ssid)
        val code = context.wifi().removeNetworkSuggestions(listOf(builder.build()))
        return code == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS
    }

    override fun updateNetwork(context: Context, wifiConfig: WifiConfigurationCompat): Int {
        /**
         * [WifiManager.updateNetwork]已经无效且没有代替的方式, 以下是官方文档说明:
         * For applications targeting android.os.Build.VERSION_CODES#Q or above, this API will always fail and return -1.
         */
        return -1
    }

    override fun getConfiguredNetworks(context: Context): List<WifiConfigurationCompat> {
        // 已经不允许获取配置列表了
        return emptyList()
    }

    override fun reconnect(context: Context): Boolean {
        /**
         * [WifiManager.reconnect]已经无效且没有代替的方式, 以下是官方文档说明:
         * For applications targeting android.os.Build.VERSION_CODES#Q or above, this API will always fail and return false.
         */
        return false
    }
}