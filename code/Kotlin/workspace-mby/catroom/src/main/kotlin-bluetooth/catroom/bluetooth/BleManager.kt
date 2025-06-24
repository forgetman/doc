package catroom.bluetooth

import android.content.Context
import bluetooth.BluetoothStateHelper
import bluetooth.BluetoothStateHelper.Listener
import bluetoothle.central.Central
import bluetoothle.central.connector.DeviceConnector.ConnState.CONNECTED
import bluetoothle.central.connector.DeviceConnector.ConnState.CONNECTING
import bluetoothle.central.connector.DeviceConnector.ConnState.CONNECT_FAILED
import bluetoothle.central.connector.DeviceConnector.ConnState.DISCONNECTED
import bluetoothle.central.connector.DeviceConnector.ConnState.DISCONNECTING
import bluetoothle.central.connector.DeviceConnector.ConnState.IDLE
import bluetoothle.central.connector.DeviceConnector.ConnState.READY
import bluetoothle.central.scanner.Scanner
import bluetoothle.def.toUUID
import catroom.bluetooth.model.RoomState
import catroom.def.Constants
import compat.bluetooth.BluetoothCompat
import coroutine.flow.stateInForever
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import logger.L

class BleManager {

    companion object {
        private const val LOG_TAG = "BleManager"

        private const val UUID_NAME_SERVICE: String = "0000fff0-0000-1000-8000-00805f9b34fb"
        private const val UUID_NAME_WRITE: String = "0000fff2-0000-1000-8000-00805f9b34fb"
        private const val UUID_NAME_NOTIFY: String = "0000fff1-0000-1000-8000-00805f9b34fb"
    }

    private lateinit var central: Central
    private lateinit var stateHelper: BluetoothStateHelper

    private var initializer: Boolean = false

    private val _roomState = MutableStateFlow(RoomState())
    val roomState = _roomState.asStateFlow()

    private val _connectState = MutableStateFlow(IDLE)
    val connectState = _connectState.map {
        when (it) {
            IDLE -> "未连接"
            CONNECTING -> "连接中"
            CONNECT_FAILED -> "连接失败"
            CONNECTED -> "已连接"
            READY -> "运行中"
            DISCONNECTING -> "断开中"
            DISCONNECTED -> "已断开"
        }
    }.stateInForever(SharingStarted.WhileSubscribed(), "未连接")


    fun init(context: Context, scope: CoroutineScope) {
        if (initializer) return
        initializer = true

        central = Central(context)
        stateHelper = BluetoothStateHelper(context)

        val listener = Listener { state ->
            L.d(LOG_TAG, "蓝牙状态变化, state = $state")
            when (state) {
                BluetoothStateHelper.State.ON -> connect()
                BluetoothStateHelper.State.OFF -> stopAll()
                else -> Unit
            }
        }
        stateHelper.addListener(listener)
        scope.observeCancel {
            stopAll()
            initializer = false
            stateHelper.removeListener(listener)
        }

        BluetoothCompat.enable(context)
    }

    private fun startScan() {
        central.startScan {
            scanMode = Scanner.ScanMode.HIGH
            uuids(UUID_NAME_SERVICE)

            onEnd {
                L.d(LOG_TAG, "startScan, onEnd")
            }

            onResult { result ->
                L.d(LOG_TAG, "startScan onResult: ${result.address}")
                central.stopScan()
                connectToDevice(result.address)
            }
        }
    }

    private fun connectToDevice(address: String) {
        central.connect {
            requestBond = false
            this.address = address

            requireSupportedService { gatt, _ ->
                L.d(LOG_TAG, "检查服务是否存在")
                gatt.services.forEach {
                    L.d(LOG_TAG, "service uuid = ${it.uuid}")
                }
                val service = gatt.getService(UUID_NAME_SERVICE.toUUID()) ?: return@requireSupportedService null
                service.characteristics.forEach {
                    L.d(LOG_TAG, "characteristic uuid = ${it.uuid}")
                }
                if (service.getCharacteristic(UUID_NAME_WRITE.toUUID()) != null
                    && service.getCharacteristic(UUID_NAME_NOTIFY.toUUID()) != null
                ) {
                    service
                } else {
                    null
                }
            }

            onInitialize {
                central.onNotification(UUID_NAME_NOTIFY, onValue = { value ->
                    onNotificationReceived(value)
                }, onFail = { _, status ->
                    L.d(LOG_TAG, "onNotification, status = $status")
                })
            }

            onConnStateChanged { state, _ ->
                L.d(LOG_TAG, "connectToDevice, state = $state")
                _connectState.value = state
                if (state == DISCONNECTED) {
                    connect()
                }
            }
        }
    }

    private fun onNotificationReceived(value: ByteArray) {
        val state: String = bytesToBinary(value, 5, 6)
        val battery: Int = value[11].toInt() and 0xFF
        val batterys = Integer.toHexString(battery)
        val batteryMsg = batterys.toInt(16)

        val roomState = RoomState(
            state[15].toString().toInt(),
            state[14].toString().toInt(),
            state[13].toString().toInt(),
            state[12].toString().toInt(),
            state[11].toString().toInt(),
            batteryMsg,
        )
        _roomState.value = roomState
    }

    private fun stopAll() {
        central.disconnect()
        central.stopScan()
    }

    private fun connect() {
        startScan()
    }

    private fun bytesToBinary(byteArray: ByteArray, index1: Int, index2: Int): String {
        val binary1 = String.format("%8s", Integer.toBinaryString(byteArray[index1].toInt() and 0xFF)).replace(' ', '0')
        val binary2 = String.format("%8s", Integer.toBinaryString(byteArray[index2].toInt() and 0xFF)).replace(' ', '0')
        return binary1 + binary2
    }

    fun turnOnLight() {
        send(Constants.Ble.BT_LIGHT_OPEN)
    }

    fun turnOffLight() {
        send(Constants.Ble.BT_LIGHT_CLOSE)
    }

    fun feedFood() {
        send(Constants.Ble.BT_FEED_CAT_FOOD)
    }

    fun feedFreezeDried() {
        send(Constants.Ble.BT_FEED_CAT_FREEZE)
    }

    private fun send(action: String) {
        val message = ByteArray(Constants.Ble.BYTE_LENGTH)
        for (i in message.indices) {
            message[i] = Constants.Ble.BT_ADDRESS_NOT_USE
        }
        message[Constants.Ble.NUM_0] = Constants.Ble.BT_HEAD
        message[Constants.Ble.NUM_1] = Constants.Ble.BT_ADDRESS_BOARD_1
        message[Constants.Ble.NUM_2] = Constants.Ble.BT_ADDRESS_BLUETOOTH
        when (action) {
            Constants.Ble.BT_LIGHT_OPEN -> {
                message[Constants.Ble.NUM_6] = Constants.Ble.BT_SEND_LIGHT_OPEN
                message[Constants.Ble.NUM_13] = Constants.Ble.BT_AE
            }

            Constants.Ble.BT_FEED_CAT_FOOD -> {
                message[Constants.Ble.NUM_8] = Constants.Ble.BT_SEND_LIGHT_OPEN
                if (_roomState.value.lightState != 0) {
                    message[Constants.Ble.NUM_6] = Constants.Ble.BT_SEND_LIGHT_OPEN
                    message[Constants.Ble.NUM_13] = Constants.Ble.BT_AF
                } else {
                    message[Constants.Ble.NUM_13] = Constants.Ble.BT_AE
                }
            }

            Constants.Ble.BT_FEED_CAT_FREEZE -> {
                message[Constants.Ble.NUM_10] = Constants.Ble.BT_SEND_LIGHT_OPEN
                if (_roomState.value.lightState != 0) {
                    message[Constants.Ble.NUM_6] = Constants.Ble.BT_SEND_LIGHT_OPEN
                    message[Constants.Ble.NUM_13] = Constants.Ble.BT_AF
                } else {
                    message[Constants.Ble.NUM_13] = Constants.Ble.BT_AE
                }
            }

            Constants.Ble.BT_LIGHT_CLOSE -> {
                message[Constants.Ble.NUM_13] = Constants.Ble.BT_AD
            }
        }
        L.d(LOG_TAG, "send, action = $action")
        central.write(UUID_NAME_WRITE, message)
    }
}