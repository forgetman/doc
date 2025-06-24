package vector.app.configuration

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import compat.window.StatusBarCompat

fun interface ContentBuilder {
    @Composable
    fun BuildContent()
}

fun interface ErrorContentBuilder {
    @Composable
    fun BuildContent(retryWith: () -> Unit)
}

/**
 * UI整体配置
 */
class UiConfig private constructor() {

    companion object {
        fun build(init: UiConfig.() -> Unit): UiConfig = UiConfig().apply(init)
    }

    var loadingContentBuilder: ContentBuilder? = null // 加载中页面
    var errorContentBuilder: ErrorContentBuilder? = null // 错误页面

    var colorScheme: ColorScheme? = null
    var shapes: Shapes? = null
    var typography: Typography? = null

    @Composable
    internal fun Theme(content: @Composable () -> Unit) {
        val view = LocalView.current
        val window = (view.context as Activity).window
        val colorScheme = colorScheme ?: MaterialTheme.colorScheme

        LaunchedEffect(colorScheme) {
            StatusBarCompat.adaptTextColorByBackground(window, colorScheme.surface.toArgb())
        }

        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes ?: MaterialTheme.shapes,
            typography = typography ?: MaterialTheme.typography
        ) {
            content()
        }
    }
}
