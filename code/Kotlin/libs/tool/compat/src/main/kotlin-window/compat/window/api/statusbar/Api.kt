package compat.window.api.statusbar

import android.view.View
import android.view.Window
import androidx.annotation.ColorInt

interface Api {
    /**
     * 设置背景颜色
     */
    fun setColor(window: Window, @ColorInt color: Int)

    /**
     * 设置文字为浅色(白色)
     */
    fun setTextColorLight(window: Window)

    /**
     * 设置文字为深色(黑色)
     */
    fun setTextColorDark(window: Window)

    /**
     * 开启沉浸式
     */
    fun flat(window: Window)

    fun addSystemUiVisibility(view: View, visibility: Int)
    fun clearSystemUiVisibility(view: View, visibility: Int)
}