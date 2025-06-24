package compat.bluetooth.api.gatt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattServer
import android.content.Context

/**
 * @author yuansui
 * @since 2023/3/18
 */
@SuppressLint("MissingPermission")
internal class ApiImpl : Api {

    override fun close(context: Context, gattServer: BluetoothGattServer): Boolean {
        gattServer.close()
        return true
    }
}