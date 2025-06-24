@file:Suppress("unused")

package vector.app.fitter

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.collection.ArrayMap
import vector.appContext
import vector.app.config.Config
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * 专门用于适配分辨率, 根据dp来设置的
 *
 * @author yuansui
 */
internal class DpFitter {

    companion object {
        private val mapMode = ArrayMap<Mode, DpComputer>()

        @JvmStatic
        fun get(mode: Mode = Config.fit().mode): DpComputer {
            var computer = mapMode[mode]
            if (computer == null) {
                computer = when (mode) {
                    Mode.WIDTH -> DpComputerWidthImpl()
                    Mode.HEIGHT -> DpComputerHeightImpl()
                    Mode.FULL_SCREEN -> DpComputerFullScreenImpl()
                    Mode.DEFAULT -> DpComputerImpl()
                }
                mapMode[mode] = computer
            }
            return computer
        }

        @JvmStatic
        fun get(context: Context?): DpComputer {
            return ContextDpComputerImpl(context ?: appContext)
        }
    }
}

internal interface DpComputer {
    /**
     * 根据缩放比例将dp转换成px
     */
    fun dp(dp: Float): Int

    fun dp(dp: Int): Int

    /**
     * 根据比例将dimen.xml里的值转换成px
     */
    fun dimenRes(@DimenRes id: Int): Int

    /**
     * 四舍五入的方式取整(符合context加载的规则)
     */
    fun toInt(value: Float): Int {
        if (value == 0f) return 0
        return value.roundToInt()
    }
}

internal abstract class ScaleDpComputer : DpComputer {
    internal abstract val scale: Float
    internal val config get() = Config.fit()
    internal val screenWidth = config.screenWidth
    internal val screenHeight = config.screenHeight

    override fun dp(dp: Float): Int {
        return toInt(dp * scale)
    }

    override fun dp(dp: Int): Int {
        return toInt(dp * scale)
    }

    override fun dimenRes(@DimenRes id: Int): Int {
        val dimen = Fitter.appResources.getDimension(id) // px
        val dimenDp = dimen / Fitter.getMetrics(Mode.DEFAULT).density
        return toInt(dimenDp * scale)
    }
}

internal class DpComputerWidthImpl : ScaleDpComputer() {
    override val scale: Float =
        if (screenWidth > screenHeight) {
            val s = screenWidth / config.height
            config.density * s
        } else {
            val s = screenWidth / config.width
            config.density * s
        }
}

internal class DpComputerHeightImpl : ScaleDpComputer() {
    override val scale: Float =
        if (screenWidth > screenHeight) {
            val s = screenHeight / config.width
            config.density * s
        } else {
            val s = screenHeight / config.height
            config.density * s
        }
}

internal class DpComputerFullScreenImpl : ScaleDpComputer() {
    override val scale: Float =
        if (screenWidth > screenHeight) {
            val scaleW = screenWidth / config.height
            val scaleH = screenHeight / config.width
            val s = if (scaleW < scaleH) scaleW else scaleH
            config.density * s
        } else {
            val scaleW = screenWidth / config.width
            val scaleH = screenHeight / config.height
            val s = if (scaleW < scaleH) scaleW else scaleH
            config.density * s
        }
}

internal class DpComputerImpl : ScaleDpComputer() {
    override val scale: Float = config.density
}

internal class ContextDpComputerImpl(context: Context) : DpComputer {

    private val ref: WeakReference<Context> = WeakReference(context)

    override fun dp(dp: Float): Int {
        return toInt(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                ref.get()?.resources?.displayMetrics
            )
        )
    }

    override fun dp(dp: Int): Int {
        return dp(dp.toFloat())
    }

    override fun dimenRes(id: Int): Int {
        return toInt(ref.get()?.resources?.getDimension(id) ?: 0f)
    }
}
