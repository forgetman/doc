package compat.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import androidx.annotation.WorkerThread
import compat.bluetooth.api.Api
import compat.bluetooth.api.Api31Impl
import compat.bluetooth.api.Api33Impl
import compat.bluetooth.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import compat.bluetooth.api.ble.Api as BleApi
import compat.bluetooth.api.ble.Api31Impl as BleApi31Impl
import compat.bluetooth.api.ble.ApiImpl as BleApiImpl
import compat.bluetooth.api.gatt.Api as GattApi
import compat.bluetooth.api.gatt.Api31Impl as GattApi31Impl
import compat.bluetooth.api.gatt.ApiImpl as GattApiImpl

/**
 * @author yuansui
 * @since 2022/8/23
 */
object BluetoothCompat {

    object Ble {
        private val api: BleApi = when {
            isSdkAtLeast(SdkInt.S_31) -> BleApi31Impl()
            else -> BleApiImpl()
        }

        fun startAdvertising(
            context: Context,
            settings: AdvertiseSettings,
            advertiseData: AdvertiseData,
            callback: AdvertiseCallback
        ): Boolean {
            return api.startAdvertising(context, settings, advertiseData, callback)
        }

        fun stopAdvertising(context: Context, callback: AdvertiseCallback): Boolean {
            return api.stopAdvertising(context, callback)
        }
    }

    object Gatt {
        private val api: GattApi = when {
            isSdkAtLeast(SdkInt.S_31) -> GattApi31Impl()
            else -> GattApiImpl()
        }

        fun close(context: Context, gattServer: BluetoothGattServer): Boolean {
            return api.close(context, gattServer)
        }
    }

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        isSdkAtLeast(SdkInt.S_31) -> Api31Impl()
        else -> ApiImpl()
    }

    fun isEnabled(context: Context): Boolean {
        return api.isEnabled(context)
    }

    fun enable(context: Context): Boolean {
        return api.enable(context)
    }

    fun disable(context: Context): Boolean {
        return api.disable(context)
    }

    fun getAdapter(context: Context): BluetoothAdapter {
        return api.getAdapter(context)
    }

    fun setPin(device: BluetoothDevice, code: String): Boolean {
        return api.setPin(device, code)
    }

    fun setPassKey(device: BluetoothDevice, key: String): Boolean {
        return api.setPassKey(device, key)
    }

    fun setPairingConfirmation(
        context: Context,
        device: BluetoothDevice,
        confirm: Boolean
    ): Boolean {
        return api.setPairingConfirmation(context, device, confirm)
    }

    fun setName(context: Context, name: String): Boolean {
        return api.setName(context, name)
    }

    fun getName(context: Context, device: BluetoothDevice): String? {
        return api.getName(context, device)
    }

    fun getType(context: Context, device: BluetoothDevice): Int {
        return api.getType(context, device)
    }

    fun getBondState(context: Context, device: BluetoothDevice): Int {
        return api.getBondState(context, device)
    }

    @WorkerThread
    fun getBondedDevices(context: Context): Set<BluetoothDevice> {
        return api.getBondedDevices(context)
    }

    @WorkerThread
    fun getConnectedDevices(context: Context, profile: Int): List<BluetoothDevice> {
        return api.getConnectedDevices(context, profile)
    }

    fun isDiscovering(context: Context): Boolean {
        return api.isDiscovering(context)
    }

    fun startDiscovery(context: Context): Boolean {
        return api.startDiscovery(context)
    }

    fun cancelDiscovery(context: Context): Boolean {
        return api.cancelDiscovery(context)
    }

    fun createBond(context: Context, device: BluetoothDevice): Boolean {
        return api.createBond(context, device)
    }

    fun removeBond(context: Context, device: BluetoothDevice): Boolean {
        return api.removeBond(context, device)
    }

    fun openGattServer(
        context: Context,
        callback: BluetoothGattServerCallback
    ): BluetoothGattServer? {
        return api.openGattServer(context, callback)
    }

}