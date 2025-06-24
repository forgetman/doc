package vector.compat.notch.manufacturer

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.provider.Settings
import android.view.WindowManager
import logger.L
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.api.NotchInScreenListener
import vector.compat.notch.api.Api
import vector.app.util.Screen
import java.lang.reflect.Method


/**
 * 华为厂商
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImplHUAWEI : Api {

    companion object {
        /*刘海屏全屏显示FLAG*/
        const val FLAG_NOTCH_SUPPORT = 0x00010000
        const val DISPLAY_NOTCH_STATUS = "display_notch_status"
    }

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        listener.onNotchInScreen(getNotchRect(activity))
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        val layoutParams: WindowManager.LayoutParams = activity.window.attributes
        try {
            val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
            val constructor =
                layoutParamsExCls.getConstructor(WindowManager.LayoutParams::class.java)
            val layoutParamsExObj = constructor.newInstance(layoutParams)
            val method = when (mode) {
                DisplayCutoutMode.DEFAULT, DisplayCutoutMode.NEVER -> layoutParamsExCls.getMethod(
                    "clearHwFlags",
                    Int::class.javaPrimitiveType
                )
                DisplayCutoutMode.SHORT_EDGES, DisplayCutoutMode.ALWAYS -> layoutParamsExCls.getMethod(
                    "addHwFlags",
                    Int::class.javaPrimitiveType
                )
            }
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT)

        } catch (e: Exception) {
            L.e(e)
        }
    }

    private fun hasNotch(activity: Activity): Boolean {
        return try {
            val classLoader: ClassLoader = activity.classLoader
            val util = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get: Method = util.getMethod("hasNotchInScreen")
            get.invoke(util) as Boolean
        } catch (e: Exception) {
            L.e(e)
            false
        }
    }

    override fun getNotchRect(activity: Activity): Rect? {
        if (!hasNotch(activity)) return null

        return try {
            val cl: ClassLoader = activity.classLoader
            val util = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = util.getMethod("getNotchSize")
            val ret = get.invoke(util) as IntArray
            // 默认在屏幕中间
            Rect((Screen.width - ret[0]) / 2, 0, ret[0], ret[1])
        } catch (e: Exception) {
            L.e(e)
            null
        }
    }

    /**
     * 获取默认和隐藏刘海区开关值
     *
     * @param context
     * @return 0表示“默认”，1表示“隐藏显示区域”
     */
    fun getIsNotchSwitchOpen(context: Context): Int {
        return Settings.Secure.getInt(context.contentResolver, DISPLAY_NOTCH_STATUS, 0)
    }
}