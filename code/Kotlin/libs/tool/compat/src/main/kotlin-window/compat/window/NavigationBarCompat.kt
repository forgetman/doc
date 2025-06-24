package compat.window

import android.view.Window
import androidx.annotation.ColorInt
import compat.window.api.navigationbar.Api
import compat.window.api.navigationbar.Api30Impl
import compat.window.api.navigationbar.Api35Impl
import compat.window.api.navigationbar.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2025/4/30
 */
object NavigationBarCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.V_35) -> Api35Impl()
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        else -> ApiImpl()
    }

    /**
     * 设置底部导航栏颜色 同时改变状态以保证设置能成功
     */
    fun setColor(window: Window, @ColorInt color: Int) = api.setColor(window, color)
}