package vector.app.compose.ext.ui

import android.view.Window
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import compat.window.WindowInsetsCompat

/**
 * The space, in pixels, at the bottom of the window that the inset represents.
 */
val WindowInsets.Companion.safeContentBottom: Int
    @Composable get() = WindowInsets.safeContent.getBottom(LocalDensity.current)

val WindowInsets.Companion.safeContentTop: Int
    @Composable get() = WindowInsets.safeContent.getTop(LocalDensity.current)

val WindowInsets.Companion.statusBarsTop: Int
    @Composable get() = WindowInsets.statusBars.getTop(LocalDensity.current)

val WindowInsets.Companion.statusBarsBottom: Int
    @Composable get() = WindowInsets.statusBars.getBottom(LocalDensity.current)

internal lateinit var androidWindowInsets: AndroidWindowInsets

val WindowInsets.Companion.safeContentTopValue: Int
    get() = androidWindowInsets.safeContentTop

val WindowInsets.Companion.safeContentBottomValue: Int
    get() = androidWindowInsets.safeContentBottom

val WindowInsets.Companion.statusBarsTopValue: Int
    get() = androidWindowInsets.statusBarsTop

class AndroidWindowInsets(private val window: Window) {
    val statusBarsTop: Int
        get() = WindowInsetsCompat.statusBarsTop(window)

    val safeContentTop: Int
        get() = WindowInsetsCompat.safeContentTop(window)

    val safeContentBottom: Int
        get() = WindowInsetsCompat.safeContentBottom(window)
}
