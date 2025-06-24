package compat.bluetooth.api

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.bluetooth
import compat.ext.checkConnectPermission
import compat.ext.checkScanPermission
import sugar.ext.isSystemApplication

/**
 * @author yuansui
 * @since 2023/3/4
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.S)
internal class Api31Impl : Api by ApiImpl() {

    override fun enable(context: Context): Boolean {
        if (!context.checkConnectPermission()) return false
        return getAdapter(context).enable()
    }

    override fun disable(context: Context): Boolean {
        if (!context.checkConnectPermission()) return false
        return getAdapter(context).disable()
    }

    override fun setPairingConfirmation(
        context: Context,
        device: BluetoothDevice,
        confirm: Boolean
    ): Boolean {
        if (!context.checkConnectPermission()) return false
        if (!context.checkPrivilegedPermission()) return false
        return device.setPairingConfirmation(confirm)
    }

    override fun setName(context: Context, name: String): Boolean {
        if (!context.checkConnectPermission()) return false
        return getAdapter(context).setName(name)
    }

    override fun getName(context: Context, device: BluetoothDevice): String? {
        if (!context.checkConnectPermission()) return null
        return device.name
    }

    override fun getType(context: Context, device: BluetoothDevice): Int {
        if (!context.checkConnectPermission()) return BluetoothDevice.DEVICE_TYPE_UNKNOWN
        return device.type
    }

    override fun getBondState(context: Context, device: BluetoothDevice): Int {
        if (!context.checkConnectPermission()) return -1
        return device.bondState
    }

    override fun getBondedDevices(context: Context): Set<BluetoothDevice> {
        if (!context.checkConnectPermission()) return emptySet()
        return getAdapter(context).bondedDevices ?: emptySet()
    }

    override fun getConnectedDevices(context: Context, profile: Int): List<BluetoothDevice> {
        if (!context.checkConnectPermission()) return emptyList()
        return context.bluetooth().getConnectedDevices(profile)
    }

    override fun isDiscovering(context: Context): Boolean {
        if (!context.checkScanPermission()) return false
        return getAdapter(context).isDiscovering
    }

    override fun startDiscovery(context: Context): Boolean {
        if (!context.checkScanPermission()) return false
        return getAdapter(context).startDiscovery()
    }

    override fun cancelDiscovery(context: Context): Boolean {
        if (!context.checkScanPermission()) return false
        return getAdapter(context).cancelDiscovery()
    }

    override fun createBond(context: Context, device: BluetoothDevice): Boolean {
        if (!context.checkConnectPermission()) return false
        return device.createBond()
    }

    override fun removeBond(context: Context, device: BluetoothDevice): Boolean {
        // 已经无法移除绑定
        return false
    }

    override fun openGattServer(
        context: Context,
        callback: BluetoothGattServerCallback
    ): BluetoothGattServer? {
        if (!context.checkConnectPermission()) return null
        return context.bluetooth().openGattServer(context, callback)
    }

    /**
     * [Manifest.permission.BLUETOOTH_PRIVILEGED]只支持系统级别应用, 第三方无法使用
     */
    private fun Context.checkPrivilegedPermission(): Boolean {
        if (!isSystemApplication(this.packageName)) return false
        return this.checkSelfPermission(Manifest.permission.BLUETOOTH_PRIVILEGED) == PackageManager.PERMISSION_GRANTED
    }
}