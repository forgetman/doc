package compat.window.api.navigationbar

import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import compat.ext.isLight

/**
 * @author yuansui
 * @since 2020/9/25
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.R)
internal class Api30Impl : Api {

    override fun setColor(window: Window, color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = color

        window.insetsController?.apply {
            if (color.isLight()) {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        }

    }
}