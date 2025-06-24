package compat.network.def.listener.wifi

/**
 * @author yuansui
 * @since 2023/7/17
 */
fun interface WifiConnectStateListener {
    fun onConnectStateChanged(connected: Boolean)
}