package bluetoothle.peripheral.server

import android.content.Context
import bluetoothle.def.OnBleOpError
import bluetoothle.def.OnBleOpSuccess
import bluetoothle.peripheral.listener.OnValueListener
import bluetoothle.peripheral.server.internal.*
import java.util.*

/**
 * 外设开启的ble服务器
 * @author yuansui
 * @since 2021/10/21
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class CharacteristicServer constructor(
    context: Context,
    services: List<GattService>,
    private val onServerOpenStateListener: OnServerOpenStateListener?
) {
    interface OnServerOpenStateListener {
        fun onSuccess() {}
        fun onFailure() {}
    }

    private val serverManager = ServerManager(context, services)

    fun open() {
        val result = serverManager.open()
        if (result) {
            onServerOpenStateListener?.onSuccess()
        } else {
            onServerOpenStateListener?.onFailure()
        }
    }

    fun close() {
        serverManager.release()
    }

    fun notify(
        uuid: UUID?,
        value: ByteArray?,
        onSuccess: OnBleOpSuccess?,
        onError: OnBleOpError?
    ): Boolean {
        if (uuid == null) return false
        return serverManager.sendNotification(uuid, value, onSuccess, onError)
    }

    fun onWrite(uuid: UUID?, listener: OnValueListener): Boolean {
        return serverManager.onWrite(uuid, listener)
    }
}
