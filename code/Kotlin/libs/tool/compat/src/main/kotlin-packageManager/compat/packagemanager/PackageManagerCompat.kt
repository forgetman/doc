package compat.packagemanager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import compat.packagemanager.api.Api
import compat.packagemanager.api.Api30Impl
import compat.packagemanager.api.Api33Impl
import compat.packagemanager.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2023/4/2
 */
object PackageManagerCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        else -> ApiImpl()
    }

    fun getPackageInfo(context: Context, packageName: String, flags: Int): PackageInfo? {
        return api.getPackageInfo(context, packageName, flags)
    }

    fun getApplicationInfo(context: Context, packageName: String, flags: Int): ApplicationInfo? {
        return api.getApplicationInfo(context, packageName, flags)
    }

    fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo> {
        return api.getInstalledPackages(context, flags)
    }
}