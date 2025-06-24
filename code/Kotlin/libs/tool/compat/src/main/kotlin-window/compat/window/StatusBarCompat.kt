package compat.window

import android.view.View
import android.view.Window
import androidx.annotation.ColorInt
import compat.ext.isDark
import compat.window.api.statusbar.Api
import compat.window.api.statusbar.Api23Impl
import compat.window.api.statusbar.Api30Impl
import compat.window.api.statusbar.Api35Impl
import compat.window.api.statusbar.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2025/4/30
 */
object StatusBarCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.V_35) -> Api35Impl()
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun setColor(window: Window, @ColorInt color: Int) = api.setColor(window, color)

    /**
     * 根据底色调整状态栏的文字颜色
     */
    fun adaptTextColorByBackground(window: Window, @ColorInt bgColor: Int) {
        if (bgColor.isDark()) {
            setTextColorLight(window)
        } else {
            setTextColorDark(window)
        }
    }

    /**
     * 设置文字为浅色(白色)
     */
    fun setTextColorLight(window: Window) = api.setTextColorLight(window)

    /**
     * 设置文字为深色(黑色)
     */
    fun setTextColorDark(window: Window) = api.setTextColorDark(window)

    /**
     * 开启沉浸式
     */
    fun flat(window: Window) = api.flat(window)

    fun addSystemUiVisibility(view: View, visibility: Int) = api.addSystemUiVisibility(view, visibility)
    fun clearSystemUiVisibility(view: View, visibility: Int) = api.clearSystemUiVisibility(view, visibility)

}