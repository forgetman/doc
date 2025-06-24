package vector.ext

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

fun Int.isDark() = ColorUtils.calculateLuminance(this) < 0.5
fun Int.isLight() = ColorUtils.calculateLuminance(this) >= 0.5

fun @receiver:ColorInt Int.withAlpha(alpha: Int): Int {
    require(alpha in 0..0xff)
    return this and 0x00ffffff or (alpha shl 24)
}

inline val @receiver:ColorInt Int.alpha get() = (this shr 24) and 0xff
inline val @receiver:ColorInt Int.red get() = (this shr 16) and 0xff
inline val @receiver:ColorInt Int.green get() = (this shr 8) and 0xff
inline val @receiver:ColorInt Int.blue get() = this and 0xff


/**
 * Return the color with 0xFF opacity.
 * E.g., 0xabcdef will be translated to 0xFFabcdef.
 */
inline val @receiver:ColorInt Int.opaque: Int get() = this or 0xff000000.toInt()



