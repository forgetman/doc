package vector.compat.notch.manufacturer

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import logger.L
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.api.Api
import vector.compat.notch.api.NotchInScreenListener
import vector.app.os.dp
import vector.app.util.Screen
import java.lang.reflect.Method

/**
 * VIVO厂家
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImplVIVO : Api {

    companion object {
        private const val VIVO_NOTCH = 0x00000020 //是否有刘海
    }

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        listener.onNotchInScreen(getNotchRect(activity))
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        // 方式未知, 待补充
    }

    @SuppressLint("PrivateApi")
    private fun hasNotch(activity: Activity): Boolean {
        return try {
            val classLoader: ClassLoader = activity.classLoader
            val ftFeature = classLoader.loadClass("android.util.FtFeature")
            val method: Method =
                ftFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            method.invoke(ftFeature, VIVO_NOTCH) as Boolean
        } catch (e: Exception) {
            L.e(e)
            false
        }
    }

    override fun getNotchRect(activity: Activity): Rect? {
        if (!hasNotch(activity)) return null
        // vivo不提供接口获取刘海尺寸，目前oppo的刘海宽为100dp, 高为27dp
        val width = 100.dp.toPx(activity.applicationContext)
        val height = 27.dp.toPx(activity.applicationContext)
        return Rect((Screen.width - width) / 2, 0, width, height)
    }
}