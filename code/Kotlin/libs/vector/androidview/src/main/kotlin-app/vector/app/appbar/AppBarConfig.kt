package vector.app.appbar

import android.content.res.ColorStateList
import vector.app.config.Config
import vector.app.os.Dimension
import vector.app.os.IntRes

class AppBarConfig private constructor() {

    companion object {
        @JvmStatic
        fun build(init: AppBarConfig.() -> Unit): AppBarConfig = AppBarConfig().apply(init)
    }

    internal val text = Text()
    internal val icon = Icon()
    internal val divider = lazy(LazyThreadSafetyMode.NONE) {
        Divider()
    }
    internal val layout = Layout()

    fun setLayout(init: Layout.() -> Unit): Layout = layout.apply(init)
    fun setIcon(init: Icon.() -> Unit): Icon = icon.apply(init)
    fun setText(init: Text.() -> Unit): Text = text.apply(init)
    fun setDivider(init: Divider.() -> Unit): Divider = divider.value.apply(init)

    class Layout internal constructor() {
        var height: Dimension? = null
        var background: IntRes? = null
        var marginStart: Dimension? = null
        var marginEnd: Dimension? = null
    }

    class Icon internal constructor() {
        enum class Shape {
            SQUARE, // 正方形
            SQUARE_INSIDE, // 内切 + 正方形
            WRAP, // 自适应
        }

        var size: Dimension? = null

        var paddingStart: Dimension? = null
        var paddingEnd: Dimension? = null

        var shape: Shape = Shape.WRAP

        var background: IntRes? = null

        /**
         * inside形状的直径, [Icon.shape] = [Icon.Shape.SQUARE_INSIDE]时有效
         * @see [Layout.height] 默认使用和高度相等的设计
         */
        var diameter: Dimension? = null
            get() {
                return if (field == null) Config.appBar().layout.height else field
            }

        /**
         * 整体距离开头的距离
         */
        var groupMarginStart: Dimension? = null

        /**
         * 整体距离结束的距离
         */
        var groupMarginEnd: Dimension? = null

        /**
         * 是否自动适配正方形(如开启, 则忽略[Icon.paddingStart][Icon.paddingEnd], icon自动居中)
         */
        internal val adaptSquareShape: Boolean
            get() = when (shape) {
                Shape.SQUARE, Shape.SQUARE_INSIDE -> true
                Shape.WRAP -> false
            }
    }

    class Text {
        var textSize: Dimension? = null
        var bold: Boolean? = null

        /**
         * 文本左右padding
         */
        var paddingStart: Dimension? = null
        var paddingEnd: Dimension? = null

        // 文本和图标之间的padding
        var drawablePadding: Dimension? = null

        var drawableSize: Dimension? = null

        var textColor: IntRes? = null
        var textColorStateList: ColorStateList? = null
        var background: IntRes? = null

        var marginStart: Dimension? = null
        var marginEnd: Dimension? = null
    }

    class Divider {
        /**
         * 是否开启divider
         */
        var enabled: Boolean? = null
        var height: Dimension? = null
        var background: IntRes? = null
    }
}
