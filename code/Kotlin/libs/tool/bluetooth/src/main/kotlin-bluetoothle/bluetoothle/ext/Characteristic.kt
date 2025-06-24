package bluetoothle.ext

import android.bluetooth.BluetoothGattCharacteristic

internal fun BluetoothGattCharacteristic.hasReadProperty(): Boolean =
    hasProperty(BluetoothGattCharacteristic.PROPERTY_READ)

internal fun BluetoothGattCharacteristic.hasWriteProperty(): Boolean =
    hasProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)
            || hasProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
            || hasProperty(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)

internal fun BluetoothGattCharacteristic.hasNotifyProperty(): Boolean =
    hasProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

internal fun BluetoothGattCharacteristic.hasIndicateProperty(): Boolean =
    hasProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

internal fun BluetoothGattCharacteristic.hasProperty(property: Int): Boolean {
    return properties and property != 0
}