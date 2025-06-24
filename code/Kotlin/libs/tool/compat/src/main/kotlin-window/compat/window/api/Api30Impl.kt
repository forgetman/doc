package compat.window.api

import android.annotation.SuppressLint
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/9/24
 */
@RequiresApi(Build.VERSION_CODES.R)
internal class Api30Impl : Api {

    override fun enterFullScreen(window: Window) {
        window.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun quitFullScreen(window: Window, enableFlatBar: Boolean) {
        window.insetsController?.apply {
            show(WindowInsets.Type.systemBars())
            if (isSdkAtLeast(SdkInt.S_31)) {
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            } else {
                // 用31或以上编译会有这些问题
                @SuppressLint("WrongConstant")
                @Suppress("DEPRECATION")
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
            }
        }
    }
}