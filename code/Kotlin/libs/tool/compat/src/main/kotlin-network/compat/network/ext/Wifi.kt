@file:Suppress("DEPRECATION")

package compat.network.ext

import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import compat.network.model.wifi.WifiConfigurationCompat
import logger.L
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.util.setFieldValue
import java.util.BitSet

fun WifiConfigurationCompat.toWifiConfiguration(): WifiConfiguration {
    return WifiConfiguration().apply {
        SSID = this@toWifiConfiguration.ssid
        preSharedKey = this@toWifiConfiguration.preSharedKey
        networkId = this@toWifiConfiguration.networkId
        wepKeys = this@toWifiConfiguration.wepKeys
        wepTxKeyIndex = this@toWifiConfiguration.wepTxKeyIndex
        allowedKeyManagement = this@toWifiConfiguration.allowedKeyManagement
        allowedAuthAlgorithms = this@toWifiConfiguration.allowedAuthAlgorithms
        allowedGroupCiphers = this@toWifiConfiguration.allowedGroupCiphers
        allowedPairwiseCiphers = this@toWifiConfiguration.allowedPairwiseCiphers
        allowedProtocols = this@toWifiConfiguration.allowedProtocols

        runCatching {
            this.setFieldValue("requirePMF", this@toWifiConfiguration.requirePMF)
        }.onSuccess {
            L.d("toWifiConfiguration", "requirePMF success")
        }.onFailure {
            L.e("toWifiConfiguration", "requirePMF error", it)
        }

        status = when (this@toWifiConfiguration.status) {
            WifiConfigurationCompat.Status.CURRENT -> WifiConfiguration.Status.CURRENT
            WifiConfigurationCompat.Status.DISABLED -> WifiConfiguration.Status.DISABLED
            WifiConfigurationCompat.Status.ENABLED -> WifiConfiguration.Status.ENABLED
        }
    }
}

fun WifiConfiguration.toCompatConfiguration(): WifiConfigurationCompat {
    return WifiConfigurationCompat.Builder()
        .setSsid(SSID)
        .setShareKey(preSharedKey)
        .setNetworkId(networkId)
        .setWepKeys(wepKeys)
        .setWepTxKeyIndex(wepTxKeyIndex)
        .setRequirePMF(false) // TODO: 看是否需要拓展反射覆盖此属性
        .setAllowedKeyManagement(allowedKeyManagement)
        .setAllowedAuthAlgorithms(allowedAuthAlgorithms)
        .setAllowedGroupCiphers(allowedGroupCiphers)
        .setAllowedPairwiseCiphers(allowedPairwiseCiphers)
        .setAllowedProtocols(allowedProtocols)
        .build()
}

/**
 * 获取ssid
 */
val ScanResult.compatSsid: String
    get() = (if (isSdkAtLeast(SdkInt.T_33)) wifiSsid?.toString() else SSID) ?: ""

/**
 * 根据[BitSet.nextSetBit]的注释转换的遍历方式
 */
internal fun BitSet.iterateBitSet(callback: (Int) -> Unit) {
    var i = this.nextSetBit(0)
    while (i >= 0) {
        // 在这里处理索引 i
//        L.d("iterateBitSet, while allowedKeyManagement, i=$i")
        callback(i)
        if (i == Integer.MAX_VALUE) {
            break
        }
        i = this.nextSetBit(i + 1)
    }
}

/**
 * 添加双引号
 */
internal fun String?.plusDoubleQuote(): String {
    return "\"${this}\""
}