package compat.bluetooth.api.gatt

import android.bluetooth.BluetoothGattServer
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.checkConnectPermission

/**
 * @author yuansui
 * @since 2023/3/18
 */
@RequiresApi(Build.VERSION_CODES.S)
internal class Api31Impl : Api {

    override fun close(context: Context, gattServer: BluetoothGattServer): Boolean {
        if (!context.checkConnectPermission()) return false
        gattServer.close()
        return true
    }

}