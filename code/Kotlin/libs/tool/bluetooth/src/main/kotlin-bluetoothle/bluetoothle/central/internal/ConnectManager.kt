package bluetoothle.central.internal

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import bluetoothle.central.connector.DeviceConnector
import bluetoothle.def.Constants
import bluetoothle.def.OnBleOpError
import bluetoothle.def.OnBleOpSuccess
import bluetoothle.def.OnBleRssi
import bluetoothle.def.OnBleValue
import bluetoothle.ext.splitIfNeeded
import bluetoothle.internal.BleManagerImpl
import logger.L
import no.nordicsemi.android.ble.ValueChangedCallback
import no.nordicsemi.android.ble.annotation.DisconnectionReason
import no.nordicsemi.android.ble.annotation.WriteType
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.util.UUID

internal class ConnectManager(
    context: Context,
    private val requestBond: Boolean,
    private val bondState: DeviceConnector.BondState,
) : BleManagerImpl(context) {

    companion object {
        private const val LOG_TAG = "ConnectManager"
    }

    interface Listener {
        fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean
        fun onInitialize()
        fun onError(@DisconnectionReason reason: Int)
    }

    private var splitSize: Int = Constants.MTU_MIN - Constants.MTU_HEADER_INFO_LENGTH
    private var gatt: BluetoothGatt? = null

    internal var listener: Listener? = null

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    override fun log(priority: Int, message: String) {
        when (priority) {
            Log.INFO -> L.i(LOG_TAG, message)
            Log.WARN -> L.w(LOG_TAG, message)
            Log.ERROR -> L.e(LOG_TAG, message)
            else -> L.d(LOG_TAG, message)
        }
    }

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return true
    }

    fun read(
        characteristic: BluetoothGattCharacteristic?, onValue: OnBleValue, onError: OnBleOpError
    ) {
        readCharacteristic(characteristic).with { _, data ->
            val bytes = data.value ?: return@with
            onValue(bytes)
        }.fail(onError).enqueue()
    }

    fun write(
        characteristic: BluetoothGattCharacteristic?,
        value: ByteArray?,
        @WriteType writeType: Int,
        onSuccess: OnBleOpSuccess,
        onError: OnBleOpError
    ) {
        writeCharacteristic(characteristic, value, writeType).done(onSuccess).fail(onError)
            .splitIfNeeded(value, splitSize).enqueue()
    }

    fun readRssi(onRssi: OnBleRssi) {
        readRssi().with(onRssi).enqueue()
    }

    fun setNotificationState(
        characteristic: BluetoothGattCharacteristic?, enable: Boolean, onSuccess: OnBleOpSuccess, onError: OnBleOpError
    ) {
        if (enable) {
            enableNotifications(characteristic).done(onSuccess).fail(onError).enqueue()
        } else {
            disableNotifications(characteristic).done(onSuccess).fail(onError).enqueue()
        }
    }

    public override fun setNotificationCallback(characteristic: BluetoothGattCharacteristic?): ValueChangedCallback {
        return super.setNotificationCallback(characteristic)
    }

    fun setIndicationState(
        characteristic: BluetoothGattCharacteristic?, enable: Boolean, onSuccess: OnBleOpSuccess, onError: OnBleOpError
    ) {
        if (enable) {
            enableIndications(characteristic).done(onSuccess).fail(onError).enqueue()
        } else {
            disableIndications(characteristic).done(onSuccess).fail(onError).enqueue()
        }
    }

    public override fun setIndicationCallback(characteristic: BluetoothGattCharacteristic?): ValueChangedCallback {
        return super.setIndicationCallback(characteristic)
    }

    fun hasService(uuid: UUID?): Boolean {
        return gatt?.getService(uuid) != null
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        L.d(LOG_TAG, "isRequiredServiceSupported")
        this.gatt = gatt
        return listener?.isRequiredServiceSupported(gatt) ?: false
    }

    override fun onServicesInvalidated() {
        L.d(LOG_TAG, "onServicesInvalidated")
        gatt = null
        error(ConnectionObserver.REASON_NOT_SUPPORTED)
    }

    override fun initialize() {
        L.d(LOG_TAG, "initialize")

        requestMtu { splitSize ->
            this.splitSize = splitSize
        }

        if (requestBond && bondState == DeviceConnector.BondState.IDLE) {
            L.d(LOG_TAG, "startBond")

            createBondInsecure().invalid {
                L.d(LOG_TAG, "bond invalid")
                error(ConnectionObserver.REASON_CANCELLED)
            }.done {
                L.d(LOG_TAG, "bond done")
            }.fail { _, status ->
                L.e(LOG_TAG, "bond fail, status = $status")
                error(ConnectionObserver.REASON_CANCELLED)
            }.enqueue()
        }
    }

    override fun onDeviceReady() {
        listener?.onInitialize()
    }

    private fun error(@DisconnectionReason reason: Int) {
        listener?.onError(reason)
    }
}