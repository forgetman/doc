package vector.app.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext

interface ComposeInflater {
    @Composable
    fun Content()

    fun compositionContext(): CompositionContext? = null
}