@file:Suppress("unused")

package vector.ext

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Process
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.systemService
import vector.util.AdditionalOptions
import vector.util.Launcher
import java.util.Locale

/**
 * 获取当前进程名
 */
fun Context.currProcessName(): String? {
    val pid = Process.myPid()
    return systemService<ActivityManager>().runningAppProcesses
        .firstOrNull {
            it.pid == pid
        }?.processName
}

/**
 * 是否为主进程
 */
fun Context.isMainProcess(): Boolean = packageName == currProcessName()

inline fun <reified T : Any> Context.intentFor() =
    Intent(this, T::class.java)

inline fun <reified T : Any> Context.startActivity(
    extras: Bundle? = null,
    options: AdditionalOptions? = null
) {
    Launcher.startActivity(this, T::class, extras, options)
}

fun Context.startActivity(
    intent: Intent,
    extras: Bundle? = null,
    options: AdditionalOptions? = null
) {
    Launcher.startActivity(this, intent, extras, options)
}

/**
 * 如果和系统重名, 就无法真正被调用
 */
inline fun <reified T : Any> Context.startServ(extras: Bundle? = null) {
    Launcher.startService(this, intentFor<T>(), extras)
}

fun Context.startServ(intent: Intent, extras: Bundle? = null) {
    Launcher.startService(this, intent, extras)
}

inline fun <reified T : Any> Context.stopService() {
    stopService(intentFor<T>())
}

enum class DayNightMode {
    DAY,
    NIGHT,
    FOLLOW_SYSTEM
}

/**
 * 设置资源加载的语言版本
 *
 * @param l
 * @return 新的context或者原有的context, 在[android.app.Activity.attachBaseContext]里使用
 */
@Suppress("DEPRECATION")
fun Context.updateLocale(l: Locale): Context {
    val resources = resources
    val config = resources.configuration

    return if (isSdkAtLeast(SdkInt.N_24)) {
        config.setLocale(l)
        createConfigurationContext(config)
    } else {
        config.locale = l
        resources.updateConfiguration(config, resources.displayMetrics)
        this
    }
}

@Suppress("DEPRECATION")
fun Context.getSystemLocale(): Locale =
    if (isSdkAtLeast(SdkInt.N_24)) {
        resources.configuration.locales[0]
    } else {
        resources.configuration.locale
    }

fun <R> Context.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?,
    block: (Cursor) -> R?
): R? {
    return contentResolver.safeQuery(uri, projection, selection, selectionArgs, sortOrder, block)
}

@RequiresApi(Build.VERSION_CODES.O)
fun <R> Context.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    queryArgs: Bundle?,
    cancellationSignal: CancellationSignal?,
    block: (Cursor) -> R?
): R? {
    return contentResolver.safeQuery(uri, projection, queryArgs, cancellationSignal, block)
}

fun <R> Context.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?,
    cancellationSignal: CancellationSignal?,
    block: (Cursor) -> R?
): R? {
    return contentResolver.safeQuery(
        uri,
        projection,
        selection,
        selectionArgs,
        sortOrder,
        cancellationSignal,
        block
    )
}

/**
 * @see [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]
 */
fun Context.getStringForLanguage(@StringRes resId: Int) = ContextCompat.getString(this, resId)

/**
 * @see [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]
 */
fun Context.getStringForLanguage(@StringRes resId: Int, vararg formatArgs: Any) =
    ContextCompat.getContextForLanguage(this).getString(resId, *formatArgs)