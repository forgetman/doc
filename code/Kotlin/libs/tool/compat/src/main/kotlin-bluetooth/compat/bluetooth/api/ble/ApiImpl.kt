package compat.bluetooth.api.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import compat.bluetooth.BluetoothCompat
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2022/8/23
 *
 * 无法检查权限(权限检查api since sdk23)
 */
@SuppressLint("MissingPermission")
internal class ApiImpl : Api {

    override fun startAdvertising(
        context: Context,
        settings: AdvertiseSettings,
        advertiseData: AdvertiseData,
        callback: AdvertiseCallback
    ): Boolean {
        BluetoothCompat.getAdapter(context).bluetoothLeAdvertiser?.startAdvertising(
            settings,
            advertiseData,
            callback
        )
        return true
    }

    override fun stopAdvertising(context: Context, callback: AdvertiseCallback): Boolean {
        val manager = context.systemService<BluetoothManager>()
        manager.adapter.bluetoothLeAdvertiser?.stopAdvertising(callback)
        return true
    }
}