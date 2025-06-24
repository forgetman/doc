package compat.network.def.listener

import compat.network.def.HotspotState
import compat.network.model.hotspot.ConnectedDevice

fun interface HotspotStateListener {
    fun onStateChanged(state: HotspotState)
}

fun interface HotspotConnectedDevicesListener {
    fun onConnectedDevicesChanged(devices: List<ConnectedDevice>)
}