package vector.app.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.IntRange
import vector.Constants
import vector.app.androidview.R
import vector.app.config.Config
import vector.app.os.IntRes
import vector.app.util.Screen
import vector.app.util.toColor
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT

/**
 * 标题导航栏
 * navigation bar
 */
class AppBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    val left: LeftLayout by lazy {
        val layout = LeftLayout(context)
        addView(layout, layout.params())
        layout
    }

    val mid: MidLayout by lazy {
        val layout = MidLayout(context)
        addView(layout, layout.params())
        layout
    }

    val midAlign: MidLayout by lazy {
        val layout = MidAlignLayout(context)
        addView(layout, layout.params())
        layout
    }

    val right: RightLayout by lazy {
        val layout = RightLayout(context)
        addView(layout, layout.params())
        layout
    }

    private val divider = lazy {
        val v = View(context).apply {
            id = R.id.app_bar_divider
            when (val bg = config.divider.value.background) {
                is IntRes.ColorInt -> setBackgroundColor(bg.value)
                is IntRes.ColorRes -> setBackgroundColor(bg.value.toColor(context))
                is IntRes.DrawableRes -> setBackgroundResource(bg.value)
                else -> {
                    // do nothing
                }
            }
        }

        v
    }

    private val config: AppBarConfig
        get() = Config.appBar()

    private fun addDivider() {
        if (divider.isInitialized()) return

        val dividerHeight = config.divider.value.height?.toPx(context) ?: 0
        if (dividerHeight != 0) {
            val params = LayoutParamsFactory.relative(MATCH_PARENT, dividerHeight)
            params.addRule(ALIGN_BOTTOM, mid.id)
            addView(divider.value, params)
        }
    }

    init {
        /**
         * 设置背景色
         */
        when (val bg = config.layout.background) {
            is IntRes.ColorInt -> setBackgroundColor(bg.value)
            is IntRes.ColorRes -> setBackgroundColor(bg.value.toColor(context))
            is IntRes.DrawableRes -> setBackgroundResource(bg.value)
            else -> {
                // do nothing
            }
        }

        if (Config.app().enableFlatBar) {
            val flatBar = View(context)
            flatBar.id = R.id.flat_bar
            addView(flatBar, LayoutParamsFactory.relative(MATCH_PARENT, Screen.statusBarHeight))
        }

        if (config.divider.value.enabled == true) addDivider()
    }

    @IntRange(from = Constants.ALPHA_MIN, to = Constants.ALPHA_MAX)
    var backgroundAlpha = 0
        set(value) {
            field = value

            background.alpha = value
            if (divider.isInitialized()) {
                divider.value.background?.alpha = value
            }
        }

    fun onlyFlatBar() {
        // 空方法, 只是为了外部显式调用, 实际为了appbar的初始化
    }
}