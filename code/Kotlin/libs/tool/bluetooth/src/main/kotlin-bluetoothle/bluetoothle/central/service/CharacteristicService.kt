package bluetoothle.central.service

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import bluetoothle.*
import bluetoothle.central.internal.ConnectManager
import bluetoothle.def.*
import logger.L
import java.util.*

/**
 * 中心设备查找到的外设能提供的特征服务
 * @author yuansui
 * @since 2021/10/14
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class CharacteristicService internal constructor(
    private val service: BluetoothGattService,
    private val manager: ConnectManager
) {
    companion object {
        private const val LOG_TAG = "CharacteristicService"
    }

    fun read(uuid: UUID, onValue: OnBleValue, onError: OnBleOpError?): Boolean {
        val char: BluetoothGattCharacteristic = service.getCharacteristic(uuid) ?: return false
        manager.read(char, onValue, onError = { device, status ->
            onError?.invoke(device, status)
        })
        return true
    }

    fun readRssi(onRssi: OnBleRssi) {
        manager.readRssi(onRssi)
    }

    fun write(
        uuid: UUID?,
        value: ByteArray?,
        writeType: WriteType = WriteType.DEFAULT,
        onSuccess: OnBleOpSuccess?,
        onError: OnBleOpError?
    ): Boolean {
        uuid ?: return false
        val char: BluetoothGattCharacteristic = service.getCharacteristic(uuid) ?: return false
        val type: Int = when (writeType) {
            WriteType.DEFAULT -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            WriteType.NO_RESPONSE -> BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            WriteType.SIGNED -> BluetoothGattCharacteristic.WRITE_TYPE_SIGNED
        }
        manager.write(char, value, type, onSuccess = {
            L.d(LOG_TAG, "write success, uuid = $uuid")
            onSuccess?.invoke(it)
        }, onError = { device, status ->
            L.d(LOG_TAG, "write error, uuid = $uuid, status = $status")
            onError?.invoke(device, status)
        })
        return true
    }

    fun onNotification(uuid: UUID?, onValue: OnBleValue, onFail: OnBleOpError?): Boolean {
        if (uuid == null) return false
        val char: BluetoothGattCharacteristic = service.getCharacteristic(uuid) ?: return false
        manager.setNotificationCallback(char).with { _, data ->
            val bytes = data.value ?: return@with
            onValue(bytes)
        }

        manager.setNotificationState(char, true, onSuccess = {
            // do nothing
        }, onError = { device, status ->
            onFail?.invoke(device, status)
        })

        return true
    }

    fun onIndication(uuidName: String, onValue: OnBleValue) {
        val uuid = uuidName.toUUID() ?: return
        onIndication(uuid, onValue)
    }

    fun onIndication(uuid: UUID, onValue: OnBleValue) {
        onIndication(uuid, onValue, null)
    }

    fun onIndication(uuidName: String, onValue: OnBleValue, onFail: OnBleOpError?) {
        val uuid = uuidName.toUUID() ?: return
        onIndication(uuid, onValue, onFail)
    }

    fun onIndication(uuid: UUID, onValue: OnBleValue, onFail: OnBleOpError?) {
        val char: BluetoothGattCharacteristic? = service.getCharacteristic(uuid)
        manager.setIndicationCallback(char).with { _, data ->
            val bytes = data.value ?: return@with
            onValue(bytes)
        }

        manager.setIndicationState(char, true, onSuccess = {
            // do nothing
        }, onError = { device, status ->
            onFail?.invoke(device, status)
        })
    }
}