@file:Suppress("unused")

package vector.app.os

import android.content.Context
import vector.app.config.Config
import vector.app.fitter.Mode

/**
 * 用来代替注释[androidx.annotation.Dimension], 更加直观且带有编译约束
 * @author yuansui
 * @since 2020/9/7
 */
sealed class DimensionSize(private val w: Int, private val h: Int) {

    fun width(context: Context?): Int {
        return if (w > 0) {
            when (this) {
                is Dp -> w.dp.toPx(context)
                is Px -> w
            }
        } else {
            w
        }
    }

    fun width(mode: Mode = Config.fit().mode): Int {
        return if (w > 0) {
            when (this) {
                is Dp -> w.dp.toPx(mode)
                is Px -> w
            }
        } else {
            w
        }
    }

    fun height(context: Context?): Int {
        return if (h > 0) {
            when (this) {
                is Dp -> h.dp.toPx(context)
                is Px -> h
            }
        } else {
            h
        }
    }

    fun height(mode: Mode = Config.fit().mode): Int {
        return if (h > 0) {
            when (this) {
                is Dp -> h.dp.toPx(mode)
                is Px -> h
            }
        } else {
            h
        }
    }

    class Dp(w: Int, h: Int) : DimensionSize(w, h)

    class Px(w: Int, h: Int) : DimensionSize(w, h)
}

sealed class DimensionSizeF(private val w: Float, private val h: Float) {

    fun width(context: Context?): Float {
        return if (w > 0) {
            when (this) {
                is Dp -> w.dp.toPx(context)
                is Px -> w
            }
        } else {
            w
        }
    }

    fun width(mode: Mode = Config.fit().mode): Float {
        return if (w > 0) {
            when (this) {
                is Dp -> w.dp.toPx(mode)
                is Px -> w
            }
        } else {
            w
        }
    }

    fun height(context: Context?): Float {
        return if (h > 0) {
            when (this) {
                is Dp -> h.dp.toPx(context)
                is Px -> h
            }
        } else {
            h
        }
    }

    fun height(mode: Mode = Config.fit().mode): Float {
        return if (h > 0) {
            when (this) {
                is Dp -> h.dp.toPx(mode)
                is Px -> h
            }
        } else {
            h
        }
    }

    class Dp(w: Float, h: Float) : DimensionSizeF(w, h)

    class Px(w: Float, h: Float) : DimensionSizeF(w, h)
}