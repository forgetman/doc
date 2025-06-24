package vector.app.compose.ext.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import vector.app.compose.ui.unit.WindowSize
import vector.app.compose.ui.unit.provideWindowSize
import vector.app.configuration.Configurations

val LocalWindowSize = compositionLocalOf<WindowSize> {
    error("CompositionLocal WindowSize not present")
}

@Composable
fun ProvideCompositionLocals(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val metrics = context.resources.displayMetrics
    val screenWidth = metrics.widthPixels
    val resolution = Configurations.resolution
    val newDensity = screenWidth / resolution.widthDensity

    val targetFontScale = 1f
    val targetDensity = Density(density = newDensity, fontScale = targetFontScale)

    val windowSize = provideWindowSize()
    CompositionLocalProvider(
        LocalDensity provides targetDensity,
        LocalWindowSize provides windowSize,
    ) {
        content()
    }
}