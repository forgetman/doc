package compat.window

import android.view.Window
import compat.window.api.Api
import compat.window.api.Api30Impl
import compat.window.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/9/24
 */
object WindowCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        else -> ApiImpl()
    }

    fun enterFullScreen(window: Window) {
        api.enterFullScreen(window)
    }

    fun quitFullScreen(window: Window, enableFlatBar: Boolean) {
        api.quitFullScreen(window, enableFlatBar)
    }
}