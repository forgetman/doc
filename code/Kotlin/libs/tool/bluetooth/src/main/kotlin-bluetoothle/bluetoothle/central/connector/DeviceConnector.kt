package bluetoothle.central.connector

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import bluetoothle.central.internal.ConnectManager
import bluetoothle.central.model.ScanResult
import bluetoothle.central.service.CharacteristicService
import bluetoothle.ext.ParingObserver
import bluetoothle.ext.ParingObserverImpl
import compat.bluetooth.BluetoothCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import logger.L
import no.nordicsemi.android.ble.ConnectRequest
import no.nordicsemi.android.ble.annotation.DisconnectionReason
import no.nordicsemi.android.ble.observer.BondingObserver
import no.nordicsemi.android.ble.observer.ConnectionObserver
import sugar.ext.runOnMainThread
import sugar.ext.runOnSubThread
import java.util.UUID

/**
 * @author yuansui
 * @since 2021/10/14
 */
class DeviceConnector private constructor(
    private val context: Context,
    private val scanResult: ScanResult?,
    private val address: String?,
    requestBond: Boolean,
    private val timeout: Long,
    private var listener: Listener?
) : ParingObserver by ParingObserverImpl(context), CoroutineScope by MainScope() {
    companion object {
        private const val LOG_TAG = "DeviceConnector"

        internal fun build(context: Context, action: Builder.() -> Unit): DeviceConnector {
            val builder = Builder()
            action(builder)
            return builder.build(context)
        }
    }

    fun interface ConnStateListener {
        fun onChanged(state: ConnState, device: BluetoothDevice)
    }

    fun interface BondStateListener {
        fun onChanged(state: BondState, device: BluetoothDevice)
    }

    fun interface RequireSupportedServiceListener {
        fun onCheck(gatt: BluetoothGatt, device: BluetoothDevice): BluetoothGattService?
    }

    fun interface Initializer {
        fun onInitialize()
    }

    enum class ConnState {
        IDLE, CONNECTING, CONNECT_FAILED, CONNECTED, READY, DISCONNECTING, DISCONNECTED,
    }

    enum class BondState {
        IDLE, // 未配对
        BONDING, // 配对中
        BONDED, // 配对成功
    }

    interface Listener {
        fun onInitialize()
        fun onConnStateChanged(state: ConnState, device: BluetoothDevice) {}
        fun onBondStateChanged(state: BondState, device: BluetoothDevice) {}
        fun requireSupportedService(
            gatt: BluetoothGatt,
            device: BluetoothDevice
        ): BluetoothGattService?
    }

    private val lock = Any()
    private var connState: ConnState = ConnState.IDLE
        set(newState) {
            synchronized(lock) {
                if (field != newState) {
                    field = newState
                    L.d(LOG_TAG, "onConnStateChanged = $newState")
                    bluetoothDevice?.let { device ->
                        listener?.let {
                            runOnMainThread(this) {
                                it.onConnStateChanged(newState, device)
                            }
                        }
                    }

                }
            }
        }

    private var bondState: BondState = BondState.IDLE
        set(newState) {
            synchronized(lock) {
                if (field != newState) {
                    field = newState
                    L.d(LOG_TAG, "onBondStateChanged = $newState")
                    bluetoothDevice?.let { device ->
                        listener?.let {
                            runOnMainThread(this) {
                                it.onBondStateChanged(newState, device)
                            }
                        }
                    }
                }
            }
        }

    private var connectRequest: ConnectRequest? = null

    private val bluetoothDevice: BluetoothDevice?
        get() {
            var device = scanResult?.device
            if (device == null && address != null) {
                device = BluetoothCompat.getAdapter(context).getRemoteDevice(address)
            }
            return device
        }

    private var supportedService: BluetoothGattService? = null
    internal var characteristicService: CharacteristicService? = null

    private val manager: ConnectManager

    init {
        runOnSubThread(this) {
            val device = bluetoothDevice
            if (device != null) {
                bondState = if (BluetoothCompat.getBondedDevices(context).contains(device)) {
                    BondState.BONDED
                } else {
                    BondState.IDLE
                }
            }
        }

        manager = ConnectManager(context, requestBond, bondState).apply {
            this.listener = object : ConnectManager.Listener {
                override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                    supportedService =
                        this@DeviceConnector.listener?.requireSupportedService(gatt, gatt.device)
                    L.d(LOG_TAG, "isRequiredServiceSupported, supportedService = $supportedService")
                    return supportedService != null
                }

                override fun onInitialize() {
                    supportedService?.let { service ->
                        characteristicService = CharacteristicService(service, this@apply)
                    }
                    this@DeviceConnector.listener?.onInitialize()
                }

                override fun onError(reason: Int) {
                    this@DeviceConnector.disconnect()
                }
            }

            connectionObserver = object : ConnectionObserver {
                override fun onDeviceConnecting(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onDeviceConnecting, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.CONNECTING
                }

                override fun onDeviceConnected(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onDeviceConnected, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.CONNECTED
                }

                override fun onDeviceFailedToConnect(
                    device: BluetoothDevice, @DisconnectionReason reason: Int
                ) {
                    L.d(LOG_TAG, "onDeviceFailedToConnect, reason = ${convertReason(reason)}")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.CONNECT_FAILED
                    unregisterParingListener()
                    supportedService = null
                    characteristicService = null
                }

                override fun onDeviceReady(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onDeviceReady, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.READY
                }

                override fun onDeviceDisconnecting(device: BluetoothDevice) {
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.DISCONNECTING
                }

                override fun onDeviceDisconnected(
                    device: BluetoothDevice, @DisconnectionReason reason: Int
                ) {
                    L.d(LOG_TAG, "onDeviceDisconnected, reason = ${convertReason(reason)}")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    connState = ConnState.DISCONNECTED
                    unregisterParingListener()
                }

                private fun convertReason(@DisconnectionReason reason: Int): String {
                    return when (reason) {
                        ConnectionObserver.REASON_SUCCESS -> "REASON_SUCCESS"
                        ConnectionObserver.REASON_TERMINATE_LOCAL_HOST -> "REASON_TERMINATE_LOCAL_HOST"
                        ConnectionObserver.REASON_TERMINATE_PEER_USER -> "REASON_TERMINATE_PEER_USER"
                        ConnectionObserver.REASON_LINK_LOSS -> "REASON_LINK_LOSS"
                        ConnectionObserver.REASON_NOT_SUPPORTED -> "REASON_NOT_SUPPORTED"
                        ConnectionObserver.REASON_CANCELLED -> "REASON_CANCELLED"
                        ConnectionObserver.REASON_TIMEOUT -> "REASON_TIMEOUT"
                        else -> "REASON_UNKNOWN"
                    }
                }
            }

            bondingObserver = object : BondingObserver {
                override fun onBondingRequired(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onBondingRequired, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    bondState = BondState.BONDING
                }

                override fun onBonded(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onBonded, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    bondState = BondState.BONDED
                }

                override fun onBondingFailed(device: BluetoothDevice) {
                    L.d(LOG_TAG, "onBondingFailed, device = $device")
                    if (device != this@DeviceConnector.bluetoothDevice) return
                    bondState = BondState.IDLE
                }
            }
        }
    }

    fun connect(): Boolean {
        L.d(LOG_TAG, "connect")
        when (connState) {
            ConnState.CONNECTED, ConnState.CONNECTING -> return false
            ConnState.IDLE, ConnState.DISCONNECTED -> {
                val device = bluetoothDevice ?: kotlin.run {
                    L.e(LOG_TAG, "找不到任何要链接的设备, 请在链接之前设置 [address]或[scanResult]")
                    return false
                }
                registerParingListener()
                sendConnectRequest(device)
            }

            else -> {
                // 暂时不处理
            }
        }
        return true
    }

    private fun sendConnectRequest(device: BluetoothDevice) {
        val request = manager.connect(device)
            .retry(3, 100)
            .timeout(timeout)
            .useAutoConnect(false)
            .then {
                connectRequest = null
            }
        request.enqueue()
        connectRequest = request
    }

    fun disconnect(): Boolean {
        if (connState == ConnState.IDLE
            || connState == ConnState.CONNECT_FAILED
            || connState == ConnState.DISCONNECTING
            || connState == ConnState.DISCONNECTED
        ) return false
        L.d(LOG_TAG, "disconnect")

        connState = ConnState.DISCONNECTING

        when {
            connectRequest != null -> {
                connectRequest?.cancelPendingConnection()
                connectRequest = null
            }

            isConnected() -> {
                manager.disconnect()
                    .invalid {
                        L.d(LOG_TAG, "disconnect, invalid")
                        connState = ConnState.DISCONNECTED
                    }
                    .then {
                        manager.close()
                        connState = ConnState.DISCONNECTED
                    }.fail { _, status ->
                        L.e(LOG_TAG, "disconnect, status = $status")
                    }.enqueue()
            }

            else -> connState = ConnState.DISCONNECTED
        }

        unregisterParingListener()

        supportedService = null
        characteristicService = null

        return true
    }

    fun release() {
        disconnect()
        manager.connectionObserver = null
        manager.listener = null
        listener = null
    }

    fun isConnected(): Boolean = manager.isConnected

    fun hasService(uuid: UUID?): Boolean {
        return manager.hasService(uuid)
    }

    class Builder {
        var scanResult: ScanResult? = null
        var address: String? = null

        /**
         * 超时, 0表示无超时机制
         */
        var timeout: Long = 0L
            set(value) {
                if (value <= 0) return
                field = value
            }

        /**
         * ble默认不需要配对也可以通信
         */
        var requestBond: Boolean = false

        private val listener: Listener = object : Listener {

            override fun onConnStateChanged(state: ConnState, device: BluetoothDevice) {
                connStateListener?.onChanged(state, device)
            }

            override fun onBondStateChanged(state: BondState, device: BluetoothDevice) {
                bondStateListener?.onChanged(state, device)
            }

            override fun requireSupportedService(
                gatt: BluetoothGatt, device: BluetoothDevice
            ): BluetoothGattService? {
                return supportedServiceListener?.onCheck(gatt, device)
            }

            override fun onInitialize() {
                initializer?.onInitialize()
            }
        }
        private var initializer: Initializer? = null
        private var connStateListener: ConnStateListener? = null
        private var bondStateListener: BondStateListener? = null
        private var supportedServiceListener: RequireSupportedServiceListener? = null

        fun onInitialize(initializer: Initializer) {
            this.initializer = initializer
        }

        fun onConnStateChanged(listener: ConnStateListener) {
            connStateListener = listener
        }

        fun onBondStateChanged(listener: BondStateListener) {
            bondStateListener = listener
        }

        fun requireSupportedService(listener: RequireSupportedServiceListener) {
            supportedServiceListener = listener
        }

        internal fun build(context: Context): DeviceConnector {
            return DeviceConnector(context, scanResult, address, requestBond, timeout, listener)
        }
    }

    fun getMtu() = manager.mtu
}