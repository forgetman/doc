package compat.bluetooth.api.ble

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2022/8/23
 */
@RequiresApi(Build.VERSION_CODES.S)
internal class Api31Impl : Api {

    override fun startAdvertising(
        context: Context,
        settings: AdvertiseSettings,
        advertiseData: AdvertiseData,
        callback: AdvertiseCallback
    ): Boolean {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) return false
        val manager = context.systemService<BluetoothManager>()
        manager.adapter.bluetoothLeAdvertiser?.startAdvertising(
            settings,
            advertiseData,
            callback
        )
        return true
    }

    override fun stopAdvertising(context: Context, callback: AdvertiseCallback): Boolean {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) return false
        val manager = context.systemService<BluetoothManager>()
        manager.adapter.bluetoothLeAdvertiser?.stopAdvertising(callback)
        return true
    }
}