package compat.window.api.navigationbar

import android.view.View
import android.view.Window
import android.view.WindowManager
import compat.ext.isLight
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/9/25
 */
@Suppress("DEPRECATION")
internal class ApiImpl : Api {

    override fun setColor(window: Window, color: Int) {
        /**
         * SDK21以上就能改变导航栏底色, 但是只有SDK26以上才能改变图标颜色
         * 为了最终的显示效果，决定只有能同时改变背景色和图标色的时候才允许修改
         */
        if (isSdkAtLeast(SdkInt.O_26)) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = color

            if (color.isLight()) {
                // is light
                val visibility = window.attributes.systemUiVisibility
                window.attributes.systemUiVisibility =
                    visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }
}