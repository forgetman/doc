package vector.util

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.StrictMode
import android.provider.Settings
import android.provider.Settings.Secure
import sugar.ext.systemService
import vector.appContext

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DeviceUtil {
    /**
     * 对大数据传输时，需要调用该方法做出判断，如果流量敏感，应该提示用户
     *
     * @return true表示流量敏感，false表示不敏感
     */
    val isActiveNetworkMetered: Boolean
        get() = appContext.systemService<ConnectivityManager>().isActiveNetworkMetered

    val runtimeMaxMemory: Long
        get() = Runtime.getRuntime().maxMemory()

    val brand: String
        get() = Build.BRAND

    val manufacturer: String
        get() = Build.MANUFACTURER

    // 获取手机型号
    val mobileType: String
        get() = Build.MODEL.replace(" ", "")

    /**
     * 获取系统版本号
     *
     * @return
     */
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    @Suppress("DEPRECATION")
    val isWifi: Boolean
        @SuppressLint("MissingPermission")
        get() = appContext.systemService<ConnectivityManager>().activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI

    val isAirplaneModeOn: Boolean
        get() = 0 != Settings.System.getInt(appContext.contentResolver, "airplane_mode_on", 0)

    val clipboardText: String?
        get() {
            val cm = appContext.systemService<ClipboardManager>()
            return cm.primaryClip?.getItemAt(0)?.text?.toString()
        }

    /**
     * 获取android id
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String? {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    fun closeStrictMode() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            // 7.0打开相机,https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
            val builder = StrictMode.VmPolicy.Builder()
            builder.detectFileUriExposure()
            StrictMode.setVmPolicy(builder.build())
        }
    }
}