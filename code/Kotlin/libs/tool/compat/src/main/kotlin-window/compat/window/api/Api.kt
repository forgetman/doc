package compat.window.api

import android.view.Window

/**
 * @author yuansui
 * @since 2020/9/24
 */
internal interface Api {
    fun enterFullScreen(window: Window)
    fun quitFullScreen(window: Window, enableFlatBar: Boolean)
}