package compat.network.def.listener.wifi

import compat.network.def.WifiState

fun interface WifiStateListener {
    fun onStateChanged(state: WifiState)
}