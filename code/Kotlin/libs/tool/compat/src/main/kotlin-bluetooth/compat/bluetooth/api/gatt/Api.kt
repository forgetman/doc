package compat.bluetooth.api.gatt

import android.bluetooth.BluetoothGattServer
import android.content.Context

/**
 * @author yuansui
 * @since 2023/3/18
 */
internal interface Api {

    fun close(context: Context, gattServer: BluetoothGattServer): Boolean
}