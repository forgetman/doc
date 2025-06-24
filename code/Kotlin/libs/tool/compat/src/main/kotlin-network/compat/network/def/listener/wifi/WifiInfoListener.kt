package compat.network.def.listener.wifi

import android.net.wifi.WifiInfo

/**
 * 获取[WifiInfo]
 */
fun interface WifiInfoListener {
    fun onWifiInfo(info: WifiInfo)
}