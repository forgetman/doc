package compat.window.api.insets

import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat

/**
 * M开始就可以利用官方的[WindowInsetsCompat]来处理了
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api by ApiImpl() {

    override fun statusBarsTop(window: Window): Int {
        val insets = window.decorView.rootWindowInsets ?: return getStatusBarTopLegacy()
        return WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.statusBars()).top
    }

    override fun safeContentTop(window: Window): Int {
        val insets = window.decorView.rootWindowInsets ?: return getStatusBarTopLegacy()
        return WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.systemBars()).top
    }

    override fun safeContentBottom(window: Window): Int {
        val insets = window.decorView.rootWindowInsets ?: return getNavigationBarBottomLegacy()
        return WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.systemBars()).bottom
    }
}