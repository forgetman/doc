package compat.window.api.statusbar

import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api by ApiImpl() {

    override fun setTextColorLight(window: Window) {
        val view = window.decorView
        clearSystemUiVisibility(view, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }

    override fun setTextColorDark(window: Window) {
        val view = window.decorView
        addSystemUiVisibility(view, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }
}