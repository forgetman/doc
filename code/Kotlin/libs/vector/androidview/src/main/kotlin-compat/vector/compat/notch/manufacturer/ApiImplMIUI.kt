package vector.compat.notch.manufacturer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.provider.Settings
import android.view.Window
import logger.L
import vector.app.util.Res
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.api.Api
import vector.compat.notch.api.NotchInScreenListener
import vector.app.util.Screen


/**
 * 小米厂家
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImplMIUI : Api {

    companion object {
        /*刘海屏全屏显示FLAG*/
        const val FLAG_NOTCH_NOT_SUPPORT = 0x00000000 // 开启配置
        const val FLAG_NOTCH_SUPPORT = 0x00000100 // 开启配置
        const val FLAG_NOTCH_PORTRAIT = 0x00000200 // 竖屏配置
        const val FLAG_NOTCH_HORIZONTAL = 0x00000400 // 横屏配置
    }

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        listener.onNotchInScreen(getNotchRect(activity))
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        val flag = when (mode) {
            DisplayCutoutMode.DEFAULT, DisplayCutoutMode.NEVER -> FLAG_NOTCH_NOT_SUPPORT
            DisplayCutoutMode.SHORT_EDGES, DisplayCutoutMode.ALWAYS -> FLAG_NOTCH_SUPPORT or FLAG_NOTCH_PORTRAIT or FLAG_NOTCH_HORIZONTAL
        }
        try {
            val method = Window::class.java.getMethod(
                "addExtraFlags",
                Int::class.javaPrimitiveType
            )
            method.invoke(activity.window, flag)
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private val notchWidth: Int by lazy {
        val id = Res.Android.getIdentifier("notch_width", Res.Type.DIMEN)
        Res.Android.getDimensionPixelSize(id)
    }

    private val notchHeight: Int by lazy {
        val id = Res.Android.getIdentifier("notch_height", Res.Type.DIMEN)
        Res.Android.getDimensionPixelSize(id)
    }

    @SuppressLint("PrivateApi")
    private fun hasNotch(): Boolean {
        return try {
            val c = Class.forName("android.os.SystemProperties")
            val get =
                c.getMethod("getInt", String::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            get.invoke(c, "ro.miui.notch", 0) == 1
        } catch (e: Exception) {
            L.e(e)
            false
        }
    }

    override fun getNotchRect(activity: Activity): Rect? {
        if (!hasNotch()) return null
        return Rect((Screen.width - notchWidth) / 2, 0, notchWidth, notchHeight)
    }

    /**
     * 判断是否隐藏屏幕刘海
     *
     * @param context
     * @return false：未隐藏刘海区域 true：隐藏了刘海区域
     */
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun getIsNotchHideOpen(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, "force_black", 0) === 1
    }
}