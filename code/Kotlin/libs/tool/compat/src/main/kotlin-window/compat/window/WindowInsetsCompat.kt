package compat.window

import android.view.Window
import compat.window.api.insets.Api
import compat.window.api.insets.Api23Impl
import compat.window.api.insets.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2025/3/11
 */
object WindowInsetsCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun statusBarsTop(window: Window): Int = api.statusBarsTop(window)
    fun safeContentTop(window: Window): Int = api.safeContentTop(window)
    fun safeContentBottom(window: Window): Int = api.safeContentBottom(window)
}