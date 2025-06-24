package compat.bluetooth.api

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import compat.ext.bluetooth
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2023/3/4
 */
@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
internal class ApiImpl : Api {

    override fun enable(context: Context): Boolean {
        return getAdapter(context).enable()
    }

    override fun disable(context: Context): Boolean {
        return getAdapter(context).disable()
    }

    override fun setPairingConfirmation(
        context: Context,
        device: BluetoothDevice,
        confirm: Boolean
    ): Boolean {
        return device.setPairingConfirmation(confirm)
    }

    override fun setName(context: Context, name: String): Boolean {
        val manager = context.systemService<BluetoothManager>()
        return manager.adapter?.setName(name) ?: false
    }

    override fun getName(context: Context, device: BluetoothDevice): String? {
        return device.name
    }

    override fun getType(context: Context, device: BluetoothDevice): Int {
        return device.type
    }

    override fun getBondState(context: Context, device: BluetoothDevice): Int {
        return device.bondState
    }

    override fun getBondedDevices(context: Context): Set<BluetoothDevice> {
        return getAdapter(context).bondedDevices ?: emptySet()
    }

    override fun getConnectedDevices(context: Context, profile: Int): List<BluetoothDevice> {
        return context.bluetooth().getConnectedDevices(profile)
    }

    override fun isDiscovering(context: Context): Boolean {
        return getAdapter(context).isDiscovering
    }

    override fun startDiscovery(context: Context): Boolean {
        return getAdapter(context).startDiscovery()
    }

    override fun cancelDiscovery(context: Context): Boolean {
        return getAdapter(context).cancelDiscovery()
    }

    override fun createBond(context: Context, device: BluetoothDevice): Boolean {
        return device.createBond()
    }

    override fun removeBond(context: Context, device: BluetoothDevice): Boolean {
        return try {
            return device::class.java.getMethod("removeBond").invoke(device) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    override fun openGattServer(
        context: Context,
        callback: BluetoothGattServerCallback
    ): BluetoothGattServer? {
        return context.bluetooth().openGattServer(context, callback)
    }
}