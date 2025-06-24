package vector.app.compose.ui.unit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext

interface WindowSize {

    /**
     * 原始的宽度(像素)
     */
    @Stable
    val width: Int

    /**
     * 原始的高度(像素)
     */
    @Stable
    val height: Int
}

@Composable
internal fun provideWindowSize(): WindowSize {
    val context = LocalContext.current
    return object : WindowSize {
        override val width: Int
            get() {
                val metrics = context.resources.displayMetrics
                return metrics.widthPixels
            }

        override val height: Int
            get() {
                val metrics = context.resources.displayMetrics
                return metrics.heightPixels
            }
    }
}