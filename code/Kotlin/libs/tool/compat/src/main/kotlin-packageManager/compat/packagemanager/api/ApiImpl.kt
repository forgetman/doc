package compat.packagemanager.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import logger.L

/**
 * @author yuansui
 * @since 2023/4/2
 */
@Suppress("DEPRECATION")
internal class ApiImpl : Api {

    override fun getPackageInfo(context: Context, packageName: String, flags: Int): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(packageName, flags)
        } catch (e: NameNotFoundException) {
            L.e(e)
            null
        }
    }

    override fun getApplicationInfo(context: Context, packageName: String, flags: Int): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(packageName, flags)
        } catch (e: NameNotFoundException) {
            L.e(e)
            null
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo> {
        return context.packageManager.getInstalledPackages(flags)
    }
}