package vector.app.os

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import vector.app.fitter.DpComputer
import vector.app.fitter.DpFitter
import vector.app.fitter.Mode
import vector.app.config.Config
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

inline val Int.dp: Dimension get() = Dimension.Dp(this)
inline val Float.dp: DimensionF get() = DimensionF.Dp(this)
inline val Double.dp: DimensionF get() = DimensionF.Dp(this.toFloat())

/**
 * @author yuansui
 * @since 2020/9/7
 */
sealed class Dimension(val value: Int) {

    /**
     * 获取px, 自动转换
     */
    fun toPx(context: Context?): Int {
        return when (this) {
            is Dp -> dp(DpFitter.get(context))
            is Px -> value
        }
    }

    fun toPx(mode: Mode = Config.fit().mode): Int {
        return when (this) {
            is Dp -> dp(DpFitter.get(mode))
            is Px -> value
        }
    }

    fun toPx(fragment: Fragment): Int {
        return when (this) {
            is Dp -> dp(DpFitter.get(fragment.context))
            is Px -> value
        }
    }

    fun toPx(view: View?): Int {
        return when (this) {
            is Dp -> dp(DpFitter.get(view?.context))
            is Px -> value
        }
    }

    private fun dp(computer: DpComputer): Int {
        return when (value) {
            MATCH_PARENT, WRAP_CONTENT -> value
            else -> computer.dp(value)
        }
    }

    class Dp(@vector.annotation.Dp value: Int) : Dimension(value)

    class Px(@androidx.annotation.Px value: Int) : Dimension(value)
}

sealed class DimensionF(val value: Float) {

    /**
     * 获取px, 自动转换
     */
    fun toPx(context: Context?): Float {
        return when (this) {
            is Dp -> dp(DpFitter.get(context))
            is Px -> value
        }
    }

    fun toPx(mode: Mode = Config.fit().mode): Float {
        return when (this) {
            is Dp -> dp(DpFitter.get(mode))
            is Px -> value
        }
    }

    fun toPx(fragment: Fragment): Float {
        return when (this) {
            is Dp -> dp(DpFitter.get(fragment.context))
            is Px -> value
        }
    }

    fun toPx(view: View?): Float {
        return when (this) {
            is Dp -> dp(DpFitter.get(view?.context))
            is Px -> value
        }
    }

    private fun dp(computer: DpComputer): Float {
        return when (value) {
            MATCH_PARENT.toFloat(), WRAP_CONTENT.toFloat() -> value
            else -> computer.dp(value).toFloat()
        }
    }

    class Dp(@vector.annotation.Dp value: Float) : DimensionF(value)

    class Px(@androidx.annotation.Px value: Float) : DimensionF(value)
}