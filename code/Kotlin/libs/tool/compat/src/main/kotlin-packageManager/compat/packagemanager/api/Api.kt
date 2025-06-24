package compat.packagemanager.api

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo

/**
 * @author yuansui
 * @since 2023/4/2
 */
internal interface Api {
    fun getPackageInfo(context: Context, packageName: String, flags: Int): PackageInfo?

    fun getApplicationInfo(context: Context, packageName: String, flags: Int): ApplicationInfo?

    fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo>
}