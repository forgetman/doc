package vector.app.os

import android.content.Context
import vector.app.util.toColor

/**
 * 用来代替注释
 * [androidx.annotation.ColorInt] / [androidx.annotation.ColorRes] / [androidx.annotation.DrawableRes]
 * 更加直观且带有编译约束
 * @author yuansui
 * @since 2021/4/9
 */
sealed class IntRes(val value: Int = 0) {
    class ColorInt(@androidx.annotation.ColorInt value: Int) : IntRes(value)
    class ColorRes(@androidx.annotation.ColorRes value: Int) : IntRes(value)
    class DrawableRes(@androidx.annotation.DrawableRes value: Int) : IntRes(value)

    /**
     * 只置换和颜色有关的属性 [ColorInt] / [ColorRes]
     * @return [androidx.annotation.ColorInt]的int
     */
    fun getIntRelatedColor(context: Context?): Int? {
        return when (this) {
            is ColorInt -> value
            is ColorRes -> value.toColor(context)
            else -> null
        }
    }
}

inline val Int.colorInt: IntRes get() = IntRes.ColorInt(this)
inline val Int.colorRes: IntRes get() = IntRes.ColorRes(this)
inline val Int.drawableRes: IntRes get() = IntRes.DrawableRes(this)