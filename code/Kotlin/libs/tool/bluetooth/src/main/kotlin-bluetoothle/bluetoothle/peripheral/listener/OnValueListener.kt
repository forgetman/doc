package bluetoothle.peripheral.listener

import android.bluetooth.BluetoothDevice

fun interface OnValueListener {
    fun onValue(device: BluetoothDevice, value: ByteArray)
}