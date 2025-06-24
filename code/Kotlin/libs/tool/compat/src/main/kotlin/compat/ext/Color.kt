package compat.ext

import androidx.core.graphics.ColorUtils

internal fun Int.isDark() = ColorUtils.calculateLuminance(this) < 0.5
internal fun Int.isLight() = ColorUtils.calculateLuminance(this) >= 0.5