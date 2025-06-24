package compat.packagemanager.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import androidx.annotation.RequiresApi
import logger.L

/**
 * @author yuansui
 * @since 2023/4/2
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class Api33Impl : Api {

    override fun getPackageInfo(context: Context, packageName: String, flags: Int): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } catch (e: NameNotFoundException) {
            L.e(e)
            null
        }
    }

    override fun getApplicationInfo(context: Context, packageName: String, flags: Int): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(flags.toLong())
            )
        } catch (e: NameNotFoundException) {
            L.e(e)
            null
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo> {
        val check =
            context.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
        if (!check) return emptyList()
        return try {
            context.packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
        } catch (e: UnsupportedOperationException) {
            L.e(e)
            emptyList()
        }
    }
}