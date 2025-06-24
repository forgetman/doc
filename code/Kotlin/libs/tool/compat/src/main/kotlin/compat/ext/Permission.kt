package compat.ext

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker

@RequiresApi(Build.VERSION_CODES.S)
internal fun Context.checkConnectPermission(): Boolean {
    return this.checkPermission(Manifest.permission.BLUETOOTH_CONNECT)
}

@RequiresApi(Build.VERSION_CODES.S)
internal fun Context.checkScanPermission(): Boolean {
    return this.checkPermission(Manifest.permission.BLUETOOTH_SCAN)
}

internal fun Context.checkPermission(permission: String): Boolean {
    return PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED
}