package compat.window.api.statusbar

import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
internal class Api35Impl : Api by Api30Impl() {

    /**
     * 针对原来的 window.statusBarColor = color 方法, 以下是官方文档解释:
     * If the app targets VANILLA_ICE_CREAM or above, the color will be transparent and cannot be changed.
     */
    override fun setColor(window: Window, color: Int) {
        window.insetsController?.apply {
            // 状态栏只能设置透明或非透明, 想要展示自定义的颜色, 只要设置为透明即可
            if (color == Color.TRANSPARENT) {
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    }

    override fun flat(window: Window) {
        // do nothing
    }
}