package reader.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
val Dimen.defaults: DimenDefaults
    @Composable @ReadOnlyComposable get() = LocalDimenDefaults.current

private val LocalDimenDefaults = staticCompositionLocalOf { DimenDefaults() }

class DimenDefaults internal constructor(
    val appBarHeight: Dp = 44.dp,
    val buttonWidthLarge: Dp = 326.dp,
    val buttonHeightLarge: Dp = 44.dp,

    val marginLevel1: Dp = 14.dp,
    val marginLevel2: Dp = 16.dp,
    val marginLevel4: Dp = 24.dp,
    val marginLevel5: Dp = 32.dp,

    val loadingIcon: Dp = 40.dp,
    val loadingIconContainer: Dp = 100.dp,
)