package bluetoothle.peripheral.server.internal

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import bluetoothle.def.Constants
import bluetoothle.def.OnBleOpError
import bluetoothle.def.OnBleOpSuccess
import bluetoothle.ext.hasWriteProperty
import bluetoothle.ext.splitIfNeeded
import bluetoothle.internal.BleManagerImpl
import bluetoothle.peripheral.listener.OnValueListener
import logger.L
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.WriteRequest
import no.nordicsemi.android.ble.observer.ServerObserver
import java.util.*

internal class ServerManager(
    private val context: Context,
    bleGattServices: List<GattService>,
) : BleServerManager(context), ServerObserver {

    companion object {
        private const val LOG_TAG = "ServerManager"
    }

    private val services = mutableListOf<BluetoothGattService>()
    private val serverConnections = mutableMapOf<String, ServerConnection>()
    private val serveValueListeners = mutableMapOf<UUID, OnValueListener>()
    private var splitSize: Int = Constants.MTU_MIN - Constants.MTU_HEADER_INFO_LENGTH


    init {
        bleGattServices.forEach {
            val service = BluetoothGattService(it.uuid, it.serviceType)

            it.characteristics.forEach { bleChar ->
                service.addCharacteristic(
                    characteristic(
                        bleChar.uuid, bleChar.properties, bleChar.permissions
                    )
                )
            }

            services.add(service)
        }
    }

    override fun initializeServer(): List<BluetoothGattService> {
        setServerObserver(this)
        return services
    }

    override fun onServerReady() {
        L.d(LOG_TAG, "onServerReady")
    }

    override fun onDeviceConnectedToServer(device: BluetoothDevice) {
        val address = device.address
        L.d(LOG_TAG, "onDeviceConnectedToServer = $address")
        serverConnections[address] = ServerConnection(device).apply {
            useServer(this@ServerManager)
            attachClientConnection(device)
        }
    }

    override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
        val address = device.address
        L.d(LOG_TAG, "onDeviceDisconnectedFromServer = $address")
        serverConnections.remove(address)?.close()
    }

    fun sendNotification(
        uuid: UUID,
        value: ByteArray?,
        onSuccess: OnBleOpSuccess?,
        onError: OnBleOpError?
    ): Boolean {
        var findChar = false

        for (i in 0 until services.size) {
            val gattService = services[i]
            val char: BluetoothGattCharacteristic? = gattService.getCharacteristic(uuid)
            if (char != null) {
                findChar = true

                serverConnections.values.forEach { serverConnection ->
                    try {
                        serverConnection.sendNotification(char, value).done {
                            L.d(LOG_TAG, "sendNotification success, uuid = $uuid")
                            onSuccess?.invoke(it)
                        }.fail { device, status ->
                            L.d(LOG_TAG, "sendNotification error, uuid = $uuid, status = $status")
                            onError?.invoke(device, status)
                        }.splitIfNeeded(value, splitSize).enqueue()
                    } catch (e: Exception) {
                        L.e(LOG_TAG, "sendNotification", e)
                    }
                }

                // 找到一个就退出
                break
            }
        }

        return findChar
    }

    fun onWrite(uuid: UUID?, listener: OnValueListener): Boolean {
        if (uuid == null) return false
        serveValueListeners[uuid] = listener
        return true
    }

    private inner class ServerConnection(val device: BluetoothDevice) : BleManagerImpl(context) {

        override fun initialize() {
            L.d(LOG_TAG, "initialize")
//            requestMtu { splitSize ->
//                this@ServerManager.splitSize = splitSize
//            }

            services.flatMap { service ->
                service.characteristics
            }.filter { char ->
                char.hasWriteProperty()
            }.forEach { char ->
                setWriteCallback(char).with { _, data ->
                    L.d(LOG_TAG, "receive on write data")
                    val bytes = data.value ?: return@with
                    val callback = serveValueListeners[char.uuid]
                    callback?.onValue(device, bytes)
                }.then {
                    L.d(LOG_TAG, "on write closed")
                }
            }
        }

        public override fun sendNotification(
            serverCharacteristic: BluetoothGattCharacteristic?,
            data: ByteArray?
        ): WriteRequest {
            return super.sendNotification(serverCharacteristic, data)
        }
    }

    /**
     * 释放资源
     * PS: [close]是final的, 无法override
     */
    fun release() {
        close()
        serveValueListeners.clear()
        services.clear()
        serverConnections.clear()
    }
}