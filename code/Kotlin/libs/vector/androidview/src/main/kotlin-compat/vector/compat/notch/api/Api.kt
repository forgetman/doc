package vector.compat.notch.api

import android.app.Activity
import android.graphics.Rect
import vector.compat.notch.DisplayCutoutMode

fun interface NotchInScreenListener {
    fun onNotchInScreen(notchRect: Rect?)
}

/**
 * 刘海屏相关操作
 * @author yuansui
 * @since 2020/5/11
 */
internal interface Api {
    /**
     * 获取刘海屏使用状态
     */
    fun fetchRect(activity: Activity, listener: NotchInScreenListener)
    fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode)
    fun getNotchRect(activity: Activity): Rect?
}