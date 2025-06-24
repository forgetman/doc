package compat.window.api.navigationbar

import android.view.Window
import androidx.annotation.ColorInt

interface Api {
    /**
     * 设置底部导航栏颜色 同时改变状态以保证设置能成功
     */
    fun setColor(window: Window, @ColorInt color: Int)
}