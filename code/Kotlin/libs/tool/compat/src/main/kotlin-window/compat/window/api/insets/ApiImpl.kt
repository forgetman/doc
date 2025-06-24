package compat.window.api.insets

import android.view.Window
import androidx.core.view.WindowInsetsCompat

/**
 * M以下没有[android.view.View.getRootWindowInsets], 无法利用官方的[WindowInsetsCompat]来处理
 */
internal class ApiImpl : Api {

    override fun statusBarsTop(window: Window): Int {
        return getStatusBarTopLegacy()
    }

    override fun safeContentTop(window: Window): Int {
        // 还不存在刘海屏
        return getStatusBarTopLegacy()
    }

    override fun safeContentBottom(window: Window): Int {
        return getNavigationBarBottomLegacy()
    }
}