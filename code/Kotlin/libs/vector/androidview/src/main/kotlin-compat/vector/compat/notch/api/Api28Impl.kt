package vector.compat.notch.api

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.compat.notch.DisplayCutoutMode
import vector.app.ext.androidContentView
import vector.app.ext.displayCutout
import vector.app.ext.view.doOnApplyWindowInsets

/**
 * @author yuansui
 * @since 2020/5/11
 */
@TargetApi(Build.VERSION_CODES.P)
internal class Api28Impl : Api {

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        activity.androidContentView?.doOnApplyWindowInsets(false) { _, insets, _ ->
            listener.onNotchInScreen(getNotchRect(activity))
            insets
        }
    }

    override fun getNotchRect(activity: Activity): Rect? {
        val rects = activity.displayCutout?.boundingRects ?: return null
        // 理论上可以有双刘海屏, 上下各一个, 目前没有这样的机型, 暂时忽略
        return rects.getOrNull(0)
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        val params = activity.window.attributes
        params.layoutInDisplayCutoutMode = when (mode) {
            DisplayCutoutMode.DEFAULT -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            DisplayCutoutMode.NEVER -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            DisplayCutoutMode.SHORT_EDGES -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            DisplayCutoutMode.ALWAYS -> {
                if (isSdkAtLeast(SdkInt.R_30)) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }
        activity.window.attributes = params
    }
}