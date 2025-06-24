package compat.bluetooth.api

import android.bluetooth.*
import android.content.Context
import compat.ext.bluetooth
import logger.L
import sugar.ext.systemService
import sugar.util.reflectDeclaredMethod

/**
 * @author yuansui
 * @since 2023/3/4
 */
internal interface Api {

    companion object {
        private const val LOG_TAG = "Bluetooth_API"
    }

    fun isEnabled(context: Context): Boolean {
        return getAdapter(context).isEnabled
    }

    /**
     * 打开蓝牙
     * @return 操作是否成功, 实际蓝牙关闭为异步流程, 需监听后续状态改变
     */
    fun enable(context: Context): Boolean

    /**
     * 关闭蓝牙
     * @return 操作是否成功, 实际蓝牙关闭为异步流程, 需监听后续状态改变
     */
    fun disable(context: Context): Boolean

    fun getAdapter(context: Context): BluetoothAdapter {
        // since sdk 18
        return context.bluetooth().adapter
    }

    /**
     * 设置 Pin 码, 默认为 0000
     */
    fun setPin(device: BluetoothDevice, code: String): Boolean {
        try {
            val method = device.reflectDeclaredMethod("setPin", ByteArray::class.java)
            val result = method.invoke(device, code.toByteArray())
            L.d(LOG_TAG, "setPin, result = $result")
        } catch (e: Exception) {
            L.e(LOG_TAG, "setPin", e)
        }
        return true
    }

    fun setPassKey(
        device: BluetoothDevice,
        key: String
    ): Boolean {
        try {
            val method = device.reflectDeclaredMethod("setPassKey", ByteArray::class.java)
            val result = method.invoke(device, key.toByteArray())
            L.d(LOG_TAG, "setPassKey, result = $result")
        } catch (e: Exception) {
            L.e(LOG_TAG, "setPassKey", e)
        }
        return true
    }

    fun setPairingConfirmation(context: Context, device: BluetoothDevice, confirm: Boolean): Boolean

    fun setName(context: Context, name: String): Boolean

    fun getName(context: Context, device: BluetoothDevice): String?

    fun getType(context: Context, device: BluetoothDevice): Int

    fun getBondState(context: Context, device: BluetoothDevice): Int

    fun getBondedDevices(context: Context): Set<BluetoothDevice>

    fun getConnectedDevices(context: Context, profile: Int): List<BluetoothDevice>

    fun isDiscovering(context: Context): Boolean

    fun startDiscovery(context: Context): Boolean

    fun cancelDiscovery(context: Context): Boolean

    fun createBond(context: Context, device: BluetoothDevice): Boolean

    fun removeBond(context: Context, device: BluetoothDevice): Boolean

    fun openGattServer(context: Context, callback: BluetoothGattServerCallback): BluetoothGattServer?
}