package compat.network.def.listener.wifi

import android.net.wifi.ScanResult

fun interface WifiScanResultListener {
    fun onScanResultChanged(results: List<ScanResult>)
}