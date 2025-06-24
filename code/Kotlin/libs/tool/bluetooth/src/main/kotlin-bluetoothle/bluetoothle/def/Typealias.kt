package bluetoothle.def

import android.bluetooth.BluetoothDevice

typealias OnBleRssi = (device: BluetoothDevice, rssi: Int) -> Unit
typealias OnBleOpSuccess = (device: BluetoothDevice) -> Unit
typealias OnBleOpError = (device: BluetoothDevice, status: Int) -> Unit
typealias OnBleValue = (value: ByteArray) -> Unit