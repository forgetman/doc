package compat.window.api.statusbar

import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

/**
 * @author yuansui
 * @since 2020/9/25
 */
@Suppress("DEPRECATION")
internal class ApiImpl : Api {

    override fun setColor(window: Window, @ColorInt color: Int) {
        window.statusBarColor = color
    }

    override fun setTextColorLight(window: Window) {
        // do nothing
    }

    override fun setTextColorDark(window: Window) {
        // do nothing
    }

    override fun flat(window: Window) {
        val decorView = window.decorView
        addSystemUiVisibility(decorView, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setColor(window, Color.TRANSPARENT)
    }

    override fun addSystemUiVisibility(view: View, visibility: Int) {
        val old = view.systemUiVisibility
        view.systemUiVisibility = old or visibility
    }

    override fun clearSystemUiVisibility(view: View, visibility: Int) {
        val old = view.systemUiVisibility
        view.systemUiVisibility = old and visibility.inv() or 0
    }
}