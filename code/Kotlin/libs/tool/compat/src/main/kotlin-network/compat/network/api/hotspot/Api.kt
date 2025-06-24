package compat.network.api.hotspot

import android.content.Context
import compat.network.def.listener.HotspotConnectedDevicesListener
import compat.network.def.listener.HotspotStateListener
import compat.network.model.hotspot.ConnectedDevice
import compat.network.model.wifi.WifiConfigurationCompat

/**
 * @author yuansui
 * @since 2023/7/10
 */
internal interface Api {

    fun isEnabled(context: Context): Boolean

    fun enable(context: Context, configuration: WifiConfigurationCompat): Boolean

    fun disable(context: Context): Boolean

    fun getConnectedDevices(context: Context): List<ConnectedDevice>

    fun addOnStateChangedListener(context: Context, listener: HotspotStateListener)
    fun removeOnStateChangedListener(context: Context, listener: HotspotStateListener)

    fun addOnConnectedDevicesChangedListener(context: Context, listener: HotspotConnectedDevicesListener)
    fun removeOnConnectedDevicesChangedListener(context: Context, listener: HotspotConnectedDevicesListener)

    fun setApConfiguration(context: Context, configuration: WifiConfigurationCompat): Boolean

    fun getApConfiguration(context: Context): WifiConfigurationCompat

    /**
     *  断连并封锁设备，该设备下次连接时会报超时
     *  @param context 上下文
     *  @param device 设备
     */
    fun blockDevice(context: Context, device: ConnectedDevice): Boolean

    /**
     * 解锁设备
     * @param context 上下文
     * @param device 设备
     */
    fun unblockDevice(context: Context, device: ConnectedDevice): Boolean {
        return unblockDevice(context, device.macAddress)
    }

    /**
     * 解锁设备
     * @param context 上下文
     * @param macAddress 设备mac地址
     */
    fun unblockDevice(context: Context, macAddress: String): Boolean
}