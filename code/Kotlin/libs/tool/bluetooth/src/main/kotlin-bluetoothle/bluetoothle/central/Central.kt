@file:Suppress("MemberVisibilityCanBePrivate")

package bluetoothle.central

import android.content.Context
import bluetoothle.central.connector.DeviceConnector
import bluetoothle.central.scanner.Scanner
import bluetoothle.central.service.CharacteristicService
import bluetoothle.def.*
import logger.L
import java.util.*

/**
 * @author yuansui
 * @since 2023/2/12
 */
class Central(private val context: Context) {

    companion object {
        private const val LOG_TAG = "Central"
    }

    private var scanner: Scanner? = null
    private var connector: DeviceConnector? = null

    private val characteristicService: CharacteristicService?
        get() = connector?.characteristicService

    fun startScan(action: Scanner.Builder.() -> Unit) {
        L.d(LOG_TAG, "startScan")
        stopScan()
        scanner = Scanner.build(context, action).apply {
            start()
        }
    }

    /**
     * 停止上一个扫描
     */
    fun stopScan() {
        L.d(LOG_TAG, "stopScan")
        scanner?.stop()
        scanner = null
    }

    fun connect(action: DeviceConnector.Builder.() -> Unit) {
        L.d(LOG_TAG, "connect")
        disconnect()
        connector = DeviceConnector.build(context, action).apply {
            connect()
        }
    }

    /**
     * 停止上一个链接
     */
    fun disconnect() {
        L.d(LOG_TAG, "disconnect")
        connector?.release()
        connector = null
    }

    fun isConnected(): Boolean {
        return connector?.isConnected() ?: false
    }

    fun getMtu() = connector?.getMtu() ?: Constants.MTU_MIN

    fun release() {
        L.d(LOG_TAG, "release")
        stopScan()
        disconnect()
    }

    fun hasService(uuid: UUID): Boolean {
        return connector?.hasService(uuid) ?: false
    }

    fun hasService(uuidName: String): Boolean {
        return connector?.hasService(uuidName.toUUID()) ?: false
    }

    fun read(uuid: UUID, onValue: OnBleValue, onError: OnBleOpError? = null): Boolean {
        return characteristicService?.read(uuid, onValue, onError) ?: false
    }

    fun readRssi(onRssi: OnBleRssi) {
        characteristicService?.readRssi(onRssi)
    }

    fun write(
        uuidName: String,
        value: ByteArray?,
        writeType: WriteType = WriteType.DEFAULT,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        val uuid = uuidName.toUUID() ?: return false
        return write(uuid, value, writeType, onSuccess, onError)
    }

    fun write(
        uuid: UUID?,
        value: ByteArray?,
        writeType: WriteType = WriteType.DEFAULT,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        return characteristicService?.write(uuid, value, writeType, onSuccess, onError) ?: false
    }

    fun onNotification(uuidName: String, onValue: OnBleValue): Boolean {
        return onNotification(uuidName.toUUID(), onValue, null)
    }

    fun onNotification(uuidName: String, onValue: OnBleValue, onFail: OnBleOpError): Boolean {
        return onNotification(uuidName.toUUID(), onValue, onFail)
    }

    fun onNotification(uuid: UUID?, onValue: OnBleValue): Boolean {
        return onNotification(uuid, onValue, null)
    }

    fun onNotification(uuid: UUID?, onValue: OnBleValue, onFail: OnBleOpError? = null): Boolean {
        return characteristicService?.onNotification(uuid, onValue, onFail) ?: false
    }
}