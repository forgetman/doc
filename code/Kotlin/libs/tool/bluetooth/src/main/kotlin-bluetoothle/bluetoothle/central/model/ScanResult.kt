package bluetoothle.central.model

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid

/**
 * @author yuansui
 * @since 2021/10/14
 */
data class ScanResult(
    val address: String,
    val name: String,
    internal val device: BluetoothDevice,
    val rssi: Int
) {
    var serviceUuids: List<ParcelUuid?>? = null
        internal set
}