package compat.bluetooth.api

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author yuansui
 * @since 2023/3/4
 *
 * 适配文档:
 * https://developer.android.google.cn/reference/android/bluetooth/BluetoothAdapter?hl=en#enable()
 * 总结: 无法[enable]和[disable]
 * 只能通过[android.app.Activity.startActivityForResult]和[android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE] Intent来解决
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class Api33Impl : Api by Api31Impl() {

    override fun enable(context: Context): Boolean {
        return false
    }

    override fun disable(context: Context): Boolean {
        return false
    }
}