@file:Suppress("MemberVisibilityCanBePrivate")

package bluetoothle.peripheral

import android.bluetooth.*
import android.content.Context
import bluetooth.BluetoothStateHelper
import bluetoothle.def.OnBleOpError
import bluetoothle.def.OnBleOpSuccess
import bluetoothle.def.toUUID
import bluetoothle.ext.ParingObserver
import bluetoothle.ext.ParingObserverImpl
import bluetoothle.peripheral.advertiser.Advertiser
import bluetoothle.peripheral.listener.OnValueListener
import bluetoothle.peripheral.server.CharacteristicServer
import bluetoothle.peripheral.server.ServerBuilder
import compat.bluetooth.BluetoothCompat
import logger.L
import sugar.ext.runOnCurrThread
import sugar.ext.throwIfNull
import java.util.*

/**
 * @author yuansui
 * @since 2023/2/11
 */
class Peripheral(private val context: Context) :
    ParingObserver by ParingObserverImpl(context),
    BluetoothStateHelper.Listener {

    companion object {
        private const val LOG_TAG = "Peripheral"
    }

    fun interface OnDeviceConnectedListener {
        fun onConnected(device: BluetoothDevice)
    }

    fun interface OnDeviceDisconnectedListener {
        fun onDisconnected(device: BluetoothDevice)
    }

    private var advertiser: Advertiser? = null
    private var server: CharacteristicServer? = null
    private var onDeviceConnectedListener: OnDeviceConnectedListener? = null
    private var onDeviceDisconnectedListener: OnDeviceDisconnectedListener? = null

    private var useGattServer: Boolean = false
    private var deviceServer: BluetoothGattServer? = null

    private val bluetoothStateHelper = BluetoothStateHelper(context)

    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
                L.d(LOG_TAG, "有设备连上 = ${device.address}")
                onDeviceConnectedListener?.onConnected(device)
            } else {
                L.d(LOG_TAG, "有设备断开 = ${device.address}, status = $status")
                onDeviceDisconnectedListener?.onDisconnected(device)
            }
        }
    }

    init {
        runOnCurrThread {
            // 防止context还没初始化完, 延迟使用
            bluetoothStateHelper.addListener(this@Peripheral)
        }
    }

    fun startAdvertising(action: Advertiser.Builder.() -> Unit) {
        advertiser?.stop()
        advertiser = Advertiser.build(context, action).apply {
            start()
        }
    }

    fun stopAdvertising() {
        advertiser?.stop()
        advertiser = null
    }

    fun openServer(action: ServerBuilder.() -> Unit) {
        // 关闭上一个(暂时只支持一个service)
        server?.close()

        val builder = ServerBuilder()
        action(builder)
        server = builder.build(context).apply {
            open()
        }

        registerParingListener()
    }

    fun closeServer() {
        server?.close()
        server = null
        unregisterParingListener()
    }

    fun onWrite(uuidName: String?, listener: OnValueListener) {
        val uuid = uuidName?.toUUID().throwIfNull("uuid无效")
        onWrite(uuid, listener)
    }

    fun onWrite(uuid: UUID?, listener: OnValueListener): Boolean {
        return server?.onWrite(uuid, listener) ?: false
    }

    fun notify(
        uuidName: String,
        value: String?,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        val uuid = uuidName.toUUID()
        return notify(uuid, value, onSuccess, onError)
    }

    fun notify(
        uuidName: String,
        value: ByteArray?,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        val uuid = uuidName.toUUID()
        return notify(uuid, value, onSuccess, onError)
    }

    fun notify(
        uuid: UUID?,
        value: String?,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        val bytes = value?.toByteArray()
        return notify(uuid, bytes, onSuccess, onError)
    }

    fun notify(
        uuid: UUID?,
        value: ByteArray?,
        onSuccess: OnBleOpSuccess? = null,
        onError: OnBleOpError? = null
    ): Boolean {
        if (uuid == null) return false
        return server?.notify(uuid, value, onSuccess, onError) ?: false
    }


    fun onDeviceConnected(listener: OnDeviceConnectedListener) {
        ensureDeviceServer()
        onDeviceConnectedListener = listener
    }

    fun onDeviceDisconnected(listener: OnDeviceDisconnectedListener) {
        ensureDeviceServer()
        onDeviceDisconnectedListener = listener
    }

    private fun ensureDeviceServer() {
        if (useGattServer) return
        useGattServer = true

        if (deviceServer == null) {
            deviceServer = BluetoothCompat.openGattServer(context, gattServerCallback)
        }
    }

    fun release() {
        stopAdvertising()
        closeServer()

        useGattServer = false
        closeGattServerIfNeeded()

        bluetoothStateHelper.removeListener(this)
    }

    override fun onStateChanged(state: BluetoothStateHelper.State) {
        when (state) {
            BluetoothStateHelper.State.ON -> {
                if (useGattServer) {
                    deviceServer = BluetoothCompat.openGattServer(context, gattServerCallback)
                }
            }
            BluetoothStateHelper.State.OFF -> {
                // 蓝牙关闭, 系统层自动释放所有资源, 不需要手动释放
                deviceServer = null
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun closeGattServerIfNeeded() {
        deviceServer?.let {
            try {
                BluetoothCompat.Gatt.close(context, it)
            } catch (e: Exception) {
                // DeadObjectException, 暂时只发现当蓝牙被关闭之后再调用close, 就会出现这个异常
                L.e(e)
            }
        }
        deviceServer = null
    }
}