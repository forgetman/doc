@file:Suppress("unused")

package sugar.ext

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

val Context?.lifecycle: Lifecycle?
    get() {
        var context: Context? = this
        while (true) {
            when (context) {
                is LifecycleOwner -> return context.lifecycle
                !is ContextWrapper -> return null
                else -> context = context.baseContext
            }
        }
    }

val Context?.lifecycleOwner: LifecycleOwner?
    get() {
        var context: Context? = this
        while (true) {
            when (context) {
                is LifecycleOwner -> return context
                !is ContextWrapper -> return null
                else -> context = context.baseContext
            }
        }
    }

/**
 * 获取捆绑在Context上的lifecycle相关的scope
 * 只有context是[LifecycleOwner]的时候有效
 */
val Context?.coroutineScope: CoroutineScope?
    get() {
        var context: Context? = this
        while (true) {
            when (context) {
                is LifecycleOwner -> return context.lifecycleScope
                !is ContextWrapper -> return null
                else -> context = context.baseContext
            }
        }
    }

/**
 * 给Context拓展获取关于Context Service的manager
 */
inline fun <reified T> Context.systemService(): T {
    return ContextCompat.getSystemService(this, T::class.java).throwIfNull("不是系统支持的service class")
}

fun Context.isSystemApplication(packageName: String): Boolean {
    try {
        // 由于compat库的依赖关系, 无法使用compat的封装
        val packageInfo = if (isSdkAtLeast(SdkInt.T_33)) {
            this.packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_CONFIGURATIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            this.packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS)
        }
        val flags = packageInfo.applicationInfo?.flags
        if (flags == null) return false
        if (flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            return true
        }
    } catch (_: PackageManager.NameNotFoundException) {
        return false
    }
    return false
}