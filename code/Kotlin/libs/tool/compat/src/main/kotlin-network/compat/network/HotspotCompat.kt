package compat.network

import android.content.Context
import compat.network.api.hotspot.Api
import compat.network.api.hotspot.Api29Impl
import compat.network.api.hotspot.ApiImpl
import compat.network.def.listener.HotspotConnectedDevicesListener
import compat.network.def.listener.HotspotStateListener
import compat.network.model.hotspot.ConnectedDevice
import compat.network.model.wifi.WifiConfigurationCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2023/7/10
 */
object HotspotCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.Q_29) -> Api29Impl()
        else -> ApiImpl()
    }

    fun isEnabled(context: Context): Boolean = api.isEnabled(context)

    fun enable(context: Context, configuration: WifiConfigurationCompat): Boolean =
        api.enable(context, configuration)

    fun disable(context: Context): Boolean = api.disable(context)

    fun addOnStateChangedListener(context: Context, listener: HotspotStateListener) =
        api.addOnStateChangedListener(context, listener)

    fun removeOnStateChangedListener(context: Context, listener: HotspotStateListener) =
        api.removeOnStateChangedListener(context, listener)

    fun getConnectedDevices(context: Context): List<ConnectedDevice> =
        api.getConnectedDevices(context)

    fun addOnConnectedDevicesChangedListener(
        context: Context,
        listener: HotspotConnectedDevicesListener
    ) = api.addOnConnectedDevicesChangedListener(context, listener)

    fun removeOnConnectedDevicesChangedListener(
        context: Context,
        listener: HotspotConnectedDevicesListener
    ) = api.removeOnConnectedDevicesChangedListener(context, listener)

    fun setApConfiguration(context: Context, configuration: WifiConfigurationCompat): Boolean =
        api.setApConfiguration(context, configuration)

    fun getApConfiguration(context: Context): WifiConfigurationCompat =
        api.getApConfiguration(context)

    fun blockDevice(context: Context, device: ConnectedDevice): Boolean =
        api.blockDevice(context, device)

    fun unblockDevice(context: Context, device: ConnectedDevice): Boolean =
        api.unblockDevice(context, device)

    fun unblockDevice(context: Context, macAddress: String): Boolean =
        api.unblockDevice(context, macAddress)
}