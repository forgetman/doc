package compat.network.def.listener.wifi

fun interface WifiRSSIListener {
    fun onRSSIChanged(rssi: Int)
}