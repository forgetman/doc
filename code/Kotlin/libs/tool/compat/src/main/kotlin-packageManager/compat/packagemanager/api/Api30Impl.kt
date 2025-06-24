package compat.packagemanager.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.checkScanPermission

/**
 * @author yuansui
 * @since 2023/4/2
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.R)
internal class Api30Impl: Api by ApiImpl() {

    @SuppressLint("QueryPermissionsNeeded")
    override fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo> {
        val check = context.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
        if (!check) return emptyList()
        return context.packageManager.getInstalledPackages(flags)
    }
}