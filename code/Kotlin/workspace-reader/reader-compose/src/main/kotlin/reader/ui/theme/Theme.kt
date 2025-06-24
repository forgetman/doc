package reader.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

fun scheme() = darkColorScheme(
    primary = Color.White,
    secondary = ColorTokens.GrayDeep,
    secondaryContainer = ColorTokens.GrayDeep,
    background = Color.Black, // 整体的背景色
    surface = ColorTokens.BlackLight,
    surfaceContainer = ColorTokens.BlackLight,
)

//object Theme {
//    val colorScheme: ColorScheme
//        @Composable @ReadOnlyComposable get() = LocalColorScheme.current
//
//    val dimen: Dimen
//        @Composable @ReadOnlyComposable get() = LocalDemen.current
//
//    val typography: Typography
//        @Composable @ReadOnlyComposable get() = LocalTypography.current
//}

private val LocalColorScheme = staticCompositionLocalOf {
    scheme()
}
private val LocalDemen = staticCompositionLocalOf { Dimen() }
private val LocalTypography = staticCompositionLocalOf { Typography() }

class Typography(
    val size10: TextStyle = DefaultTextStyle.copy(fontSize = 10.sp, lineHeight = 14.sp),
    val size12: TextStyle = DefaultTextStyle.copy(fontSize = 12.sp, lineHeight = 17.sp),
    val size14: TextStyle = DefaultTextStyle.copy(fontSize = 14.sp, lineHeight = 20.sp),
    val size16: TextStyle = DefaultTextStyle.copy(fontSize = 16.sp, lineHeight = 22.sp),
    val size18: TextStyle = DefaultTextStyle.copy(fontSize = 18.sp, lineHeight = 25.sp),
    val size20: TextStyle = DefaultTextStyle.copy(fontSize = 20.sp, lineHeight = 28.sp),
    val size22: TextStyle = DefaultTextStyle.copy(fontSize = 22.sp, lineHeight = 30.sp),
    val size24: TextStyle = DefaultTextStyle.copy(fontSize = 24.sp, lineHeight = 33.sp),
    val size32: TextStyle = DefaultTextStyle.copy(fontSize = 32.sp, lineHeight = 45.sp),
)

private val DefaultTextStyle: TextStyle
    get() = TextStyle.Default.copy(color = ColorTokens.White85)

object ColorTokens {
    val White8: Color = Color.White.copy(alpha = 0.08f)
    val White45: Color = Color.White.copy(alpha = 0.45f)
    val White50: Color = Color.White.copy(alpha = 0.50f)
    val White58: Color = Color.White.copy(alpha = 0.58f)
    val White65: Color = Color.White.copy(alpha = 0.65f)
    val White85: Color = Color.White.copy(alpha = 0.85f)

    val Red: Color = Color(0xFFFD2D21)
    val Green: Color = Color.Green

    val BlackLight: Color = Color(0xFF100D05)
    val Black20: Color = Color.Black.copy(alpha = 0.2f)
    val Black40: Color = Color.Black.copy(alpha = 0.4f)
    val Black50: Color = Color.Black.copy(alpha = 0.5f)

    val Purple: Color = Color(0xFF5E6AB5)
    val Purple40: Color = Color(0x66414B8C)

    val GrayLight: Color = Color(0xFF8C8A8A)
    val GrayMiddle: Color = Color(0xFF292827)
    val GrayDeep: Color = Color(0xFF1A1917)
}

class Dimen