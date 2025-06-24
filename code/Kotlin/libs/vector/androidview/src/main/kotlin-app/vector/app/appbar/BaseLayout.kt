package vector.app.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import vector.app.androidview.R
import vector.app.config.Config
import vector.app.os.Dimension
import vector.app.os.IntRes
import vector.app.util.Res
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

internal typealias ClickAction = (View) -> Unit

/**
 * @author yuansui
 * @since 2018/2/24
 */
abstract class BaseLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    /**
     * 获取相对父布局的params
     */
    open fun params(): RelativeLayout.LayoutParams {
        val height = toPx(Config.appBar().layout.height, WRAP_CONTENT)
        return LayoutParamsFactory.relative(WRAP_CONTENT, height).apply {
            addRule(RelativeLayout.CENTER_VERTICAL)
            if (Config.app().enableFlatBar) addRule(RelativeLayout.BELOW, R.id.flat_bar)
        }
    }

    /**
     * 加入自定义布局
     */
    fun add(action: ViewAttrs.() -> Unit): View? {
        val attr = ViewAttrs(context)
        action(attr)
        attr.build()?.apply {
            this@BaseLayout.addView(this)
        }

        return attr.view
    }

    /**
     * 加入自定义布局
     */
    fun add(v: View): View? {
        return add {
            view = v
        }
    }

    fun addText(text: String): TextView {
        return addText {
            this.text = text
        }
    }

    fun addText(@StringRes id: Int): TextView {
        return addText(Res.getString(id, context))
    }

    fun addText(action: TextAttrs.() -> Unit): AppCompatTextView {
        val attr = TextAttrs(context)
        action(attr)
        val tv = attr.build(context)
        addView(tv, getTextParams())
        return tv
    }

    private fun getTextParams(): LayoutParams {
        val config = Config.appBar()
        val height = toPx(config.layout.height, WRAP_CONTENT)
        return LayoutParamsFactory.linear(WRAP_CONTENT, height).apply {
            gravity = Gravity.CENTER
            weight = 0f
            marginStart = toPx(config.text.marginStart)
            marginEnd = toPx(config.text.marginEnd)
        }
    }

    fun addIcon(drawable: IntRes, onClick: ClickAction? = null): View {
        val attr = IconAttrs(context)
        attr.drawable = drawable
        attr.onClick = onClick
        return attr.build(context).apply {
            if (Config.appBar().icon.adaptSquareShape) {
                this@BaseLayout.addView(this, getCommonLayoutParams())
            } else {
                adjustImageViewMargins()
                this@BaseLayout.addView(this, 1, getCommonLayoutParams())
            }
        }
    }

    fun addIcon(action: IconAttrs.() -> Unit): View {
        val attr = IconAttrs(context)
        action(attr)
        return attr.build(context).apply {
            if (Config.appBar().icon.adaptSquareShape) {
                this@BaseLayout.addView(this, getCommonLayoutParams())
            } else {
                adjustImageViewMargins()
                this@BaseLayout.addView(this, 1, getCommonLayoutParams())
            }
        }
    }

    private fun adjustImageViewMargins() {
        if (this@BaseLayout.childCount == 0) {

            val start = toPx(Config.appBar().icon.groupMarginStart)
            val end = toPx(Config.appBar().icon.groupMarginEnd)

            fun addMarginView(margin: Int) {
                addView(View(context), getMarginLayoutParams(margin))
            }

            when (this) {
                is RightLayout -> {
                    // 右边为start
                    addMarginView(end)
                    addMarginView(start)
                }

                is LeftLayout -> {
                    // 左边为start
                    addMarginView(start)
                    addMarginView(end)
                }

                else -> {
                    // do nothing
                    // 暂时不需要加, 之后看情况调整
                }
            }
        }
    }

    /**
     * 根据高度获取linear params, 如无设置高度则为WRAP
     */
    private fun getCommonLayoutParams(): LayoutParams {
        val size = toPx(Config.appBar().layout.height, WRAP_CONTENT)
        return LayoutParamsFactory.linear(
            if (Config.appBar().icon.adaptSquareShape) size else WRAP_CONTENT,
            size
        )
            .apply {
                gravity = Gravity.CENTER
                weight = 0f
            }
    }

    private fun getMarginLayoutParams(margin: Int): LayoutParams {
        val height = toPx(Config.appBar().layout.height, WRAP_CONTENT)
        return LayoutParamsFactory.linear(margin, height).apply {
            gravity = Gravity.CENTER
            weight = 0f
        }
    }

    protected fun toPx(dimen: Dimension?, default: Int? = null): Int =
        dimen?.toPx(context) ?: default ?: 0
}

class LeftLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseLayout(context, attrs, defStyleAttr) {

    init {
        id = R.id.app_bar_left
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
    }

    override fun params(): RelativeLayout.LayoutParams {
        return super.params().apply {
            marginStart = toPx(Config.appBar().layout.marginStart)
            marginEnd = toPx(Config.appBar().layout.marginEnd)
        }
    }

    fun setWidth(width: Int) {
        val params = layoutParams as? RelativeLayout.LayoutParams ?: return
        params.width = width
        if (width != MATCH_PARENT) {
            params.removeRule(RelativeLayout.LEFT_OF)
        } else {
            params.addRule(RelativeLayout.LEFT_OF, R.id.app_bar_right)
        }
        layoutParams = params
    }
}

open class MidLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseLayout(context, attrs, defStyleAttr) {

    init {
        id = R.id.app_bar_mid
    }

    override fun params(): RelativeLayout.LayoutParams {
        val params = super.params()
        params.width = MATCH_PARENT
        return params
    }
}

/**
 * 会依赖左右布局调整自身位置的layout
 */
class MidAlignLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MidLayout(context, attrs, defStyleAttr) {

    init {
        id = R.id.app_bar_mid_align
    }

    override fun params(): RelativeLayout.LayoutParams {
        return super.params().apply {
            width = MATCH_PARENT
            addRule(RelativeLayout.RIGHT_OF, R.id.app_bar_left)
            addRule(RelativeLayout.LEFT_OF, R.id.app_bar_right)
        }
    }
}

class RightLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseLayout(context, attrs, defStyleAttr) {

    init {
        id = R.id.app_bar_right
        gravity = Gravity.END or Gravity.CENTER_VERTICAL
    }

    override fun params(): RelativeLayout.LayoutParams {
        return super.params().apply {
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            marginEnd = toPx(Config.appBar().layout.marginEnd)
        }
    }
}