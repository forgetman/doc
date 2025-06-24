@file:Suppress("unused")

package vector.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import compat.packagemanager.PackageManagerCompat
import vector.EMPTY
import vector.appContext

/**
 * 关于apk的包信息
 * @author yuansui
 */
object PackageUtil {
    private const val ANDROID_MARKET_PACKAGE_NAME = "com.android.vending"

    /**
     * 是否安装了安卓市场
     *
     * @return
     */
    val isAndroidMarketAvailable: Boolean
        get() {
            val packages = PackageManagerCompat.getInstalledPackages(appContext, 0)
            return packages.indices
                .map {
                    packages[it]
                }
                .any {
                    it.packageName == ANDROID_MARKET_PACKAGE_NAME
                }
        }

    /**
     * 当前软件版本名
     */
    val appVersionName: String
        get() = packageInfo?.versionName ?: EMPTY


    /**
     * 当前软件版本号
     */
    val appVersionCode: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode ?: -1
            } else {
                @Suppress("DEPRECATION")
                (packageInfo?.versionCode ?: -1).toLong()
            }
        }

    /**
     * App的名字
     */
    val appName: CharSequence?
        get() = applicationInfo?.let { pm.getApplicationLabel(it) }

    /**
     * 获取App图标
     *
     * @return
     */
    val appIcon: Drawable?
        get() {
            return try {
                pm.getApplicationIcon(pkgName)
            } catch (e: NameNotFoundException) {
                null
            }
        }

    private val pm: PackageManager
        get() = appContext.packageManager

    private val pkgName: String
        get() = appContext.packageName

    private val packageInfo: PackageInfo?
        get() = PackageManagerCompat.getPackageInfo(appContext, pkgName, 0)

    private val applicationInfo: ApplicationInfo?
        get() = getApplicationInfo(0)

    /**
     * 获取meta信息
     * @return 不区分类型, 无条件转为string
     */
    @Suppress("DEPRECATION")
    @Deprecated("Use the type-safe specific APIs depending on the type of the item to be retrieved, eg. getString")
    fun getMetaValue(key: String?): String? =
        getApplicationInfo(PackageManager.GET_META_DATA)?.metaData?.get(key)?.toString()

    /**
     * 获取metaData对象
     * @return [android.os.Bundle]
     */
    fun getMetaData(): Bundle? = getApplicationInfo(PackageManager.GET_META_DATA)?.metaData

    /**
     * 设置meta信息
     */
    @Deprecated("只能更改内存中已经读出来的值, 不能更改声明的值")
    fun setMetaValue(key: String?, value: String) {
        getApplicationInfo(PackageManager.GET_META_DATA)?.metaData?.putString(key, value)
    }

    private fun getApplicationInfo(flags: Int): ApplicationInfo? {
        return PackageManagerCompat.getApplicationInfo(appContext, pkgName, flags)
    }

    /**
     * 检查是否安装了某个应用
     * @param packageName 应用包名
     * @param context 上下文
     * @return true: 已安装, false: 未安装
     */
    fun checkAppInstalled(packageName: String, context: Context = appContext): Boolean {
        if (packageName.isEmpty()) {
            return false
        }
        return PackageManagerCompat.getPackageInfo(context, packageName, 0) != null
    }
}
