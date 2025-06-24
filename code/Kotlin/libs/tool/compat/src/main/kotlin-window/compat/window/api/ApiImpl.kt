@file:Suppress("DEPRECATION")

package compat.window.api

import android.view.View
import android.view.Window
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/9/24
 */
internal class ApiImpl : Api {

    override fun enterFullScreen(window: Window) {
        val view = window.decorView
        var newFlag = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val oldFlag = view.systemUiVisibility
        if (oldFlag and View.SYSTEM_UI_FLAG_LAYOUT_STABLE != 0) {
            newFlag = newFlag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        if (isSdkAtLeast(SdkInt.M_23)) {
            if (oldFlag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0) {
                newFlag = newFlag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        view.systemUiVisibility = newFlag
    }

    override fun quitFullScreen(window: Window, enableFlatBar: Boolean) {
        val view = window.decorView
        var newFlag = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (enableFlatBar) {
            newFlag = newFlag or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        val oldFlag = view.systemUiVisibility
        if (oldFlag and View.SYSTEM_UI_FLAG_LAYOUT_STABLE != 0) {
            newFlag = newFlag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        if (isSdkAtLeast(SdkInt.M_23)) {
            if (oldFlag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0) {
                newFlag = newFlag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        view.systemUiVisibility = newFlag
    }
}