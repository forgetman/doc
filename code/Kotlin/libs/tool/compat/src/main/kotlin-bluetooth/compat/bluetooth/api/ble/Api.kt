package compat.bluetooth.api.ble

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context

internal interface Api {

    fun startAdvertising(
        context: Context,
        settings: AdvertiseSettings,
        advertiseData: AdvertiseData,
        callback: AdvertiseCallback
    ): Boolean

    fun stopAdvertising(context: Context, callback: AdvertiseCallback): Boolean
}