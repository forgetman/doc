package compat.window.api.statusbar

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi

/**
 * @author yuansui
 * @since 2020/9/25
 */
@RequiresApi(Build.VERSION_CODES.R)
internal class Api30Impl : Api by Api23Impl() {

    override fun setTextColorLight(window: Window) {
        window.insetsController?.apply {
            setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    override fun setTextColorDark(window: Window) {
        window.insetsController?.apply {
            setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    override fun flat(window: Window) {
        // 只需要更改颜色就可以了
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setColor(window, Color.TRANSPARENT)
    }

    override fun addSystemUiVisibility(view: View, visibility: Int) {
        // do nothing
    }

    override fun clearSystemUiVisibility(view: View, visibility: Int) {
        // do nothing
    }
}