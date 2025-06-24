@file:Suppress("MemberVisibilityCanBePrivate")

package vector.app.appbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.config.Config
import vector.app.ext.asStyle
import vector.app.ext.view.ensureIdExist
import vector.app.ext.view.setDrawablesWithIntrinsicBounds
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.os.Dimension
import vector.app.os.IntRes
import vector.app.util.toColor
import vector.app.util.toColorStateList
import vector.app.util.toDrawable
import vector.util.LayoutParamsFactory
import vector.util.WRAP_CONTENT
import kotlin.math.sqrt

abstract class BaseAttrs(val context: Context) {

    var onClick: ClickAction? = null

    internal fun View.setStateListDrawable(@ColorInt value: Int) {
        val drawable = StateListDrawable()

        val normal = ColorDrawable(Color.TRANSPARENT)
        val pressed = ColorDrawable(value)

        val pressedState = android.R.attr.state_pressed
        drawable.addState(intArrayOf(pressedState), pressed)
        drawable.addState(intArrayOf(-pressedState), normal)

        background = drawable
    }

    internal fun toPx(dimen: Dimension?, default: Int? = null): Int = dimen?.toPx(context) ?: default ?: 0
}

class ViewAttrs(context: Context) : BaseAttrs(context) {
    var width = WRAP_CONTENT
    var height = WRAP_CONTENT
    var gravity: Int? = null

    var view: View? = null

    fun build(): View? {
        val v = view ?: return null

        onClick?.let {
            v.setOnDebounceClickListener { view -> it(view) }
        }

        val params = LayoutParamsFactory.linear(width, height)
        params.gravity = gravity ?: Gravity.CENTER
        v.layoutParams = params

        return v
    }
}

class TextAttrs(context: Context) : BaseAttrs(context) {

    private val config: AppBarConfig.Text
        get() = Config.appBar().text

    @StringRes
    var textRes: Int? = null
    var text: CharSequence? = null

    /**
     * 会影响所有的drawable. 无论是left还是right
     */
    var drawableTint: IntRes? = null

    @DrawableRes
    var drawableResLeft: Int? = null
    var drawableLeft: Drawable? = null

    @DrawableRes
    var drawableResRight: Int? = null
    var drawableRight: Drawable? = null

    var drawablePadding: Int = toPx(config.drawablePadding)
    var drawableSize: Int = toPx(config.drawableSize)

    var textColor: IntRes? = config.textColor
    var textColorStateList: ColorStateList? = config.textColorStateList

    var textSize: Int = toPx(config.textSize)

    var maxTextLength: Dimension? = null

    var maxWidth: Dimension? = null

    /**
     * 最大行数
     */
    var maxLines: Int? = null

    var paddingStart: Int = toPx(config.paddingStart)
    var paddingEnd: Int = toPx(config.paddingEnd)

    var bold: Boolean? = config.bold

    var enable: Boolean? = null

    var gravity: Int? = null

    fun build(context: Context): AppCompatTextView {
        val tv = AppCompatTextView(context)

        val g = gravity
        if (g != null) {
            tv.gravity = g
        } else {
            tv.gravity = Gravity.CENTER
        }

        when {
            maxLines != null -> {
                tv.maxLines = maxLines as Int
            }

            maxTextLength != null -> {
                tv.maxWidth = toPx(maxTextLength) * textSize
                tv.setSingleLine()
                tv.ellipsize = TextUtils.TruncateAt.END
            }

            maxWidth != null -> {
                tv.maxWidth = toPx(maxWidth)
                tv.setSingleLine()
                tv.ellipsize = TextUtils.TruncateAt.END
            }

            else -> {
                tv.setSingleLine()
                tv.ellipsize = TextUtils.TruncateAt.END
            }
        }

        // 设置文字大小
        if (textSize != 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }

        // 设置文字颜色
        val colorStateList = textColorStateList
        if (colorStateList != null) {
            tv.setTextColor(colorStateList)
        } else {
            when (val color = textColor) {
                is IntRes.ColorInt -> tv.setTextColor(color.value)
                is IntRes.ColorRes -> tv.setTextColor(color.value.toColorStateList(context))
                else -> {
                    // ignore null and DrawableRes
                }
            }
        }

        tv.setPadding(paddingStart, 0, paddingEnd, 0)

        bold?.let { tv.paint.isFakeBoldText = it }

        enable?.let { tv.isEnabled = it }

        onClick?.let {
            tv.setOnDebounceClickListener { view -> it(view) }
        }

        val textId = textRes
        if (textId != null && textId != 0) {
            tv.setText(textId)
        } else {
            tv.text = text
        }

        tv.compoundDrawablePadding = drawablePadding
        val iconSize = drawableSize
        if (iconSize != 0) {
            fun getDrawableInBounds(@DrawableRes drawableRes: Int?, drawable: Drawable?): Drawable? {
                val d: Drawable = if (drawableRes == null || drawableRes == 0) {
                    // 使用drawable
                    drawable ?: return null
                } else {
                    drawableRes.toDrawable(context) ?: return null
                }

                val w = d.intrinsicWidth
                val h = d.intrinsicHeight
                val scale = if (w > h) {
                    h / iconSize.toFloat()
                } else {
                    w / iconSize.toFloat()
                }
                d.setBounds(0, 0, (w * scale).toInt(), (h * scale).toInt())

                return d
            }

            tv.setCompoundDrawables(
                getDrawableInBounds(drawableResLeft, drawableLeft),
                null,
                getDrawableInBounds(drawableResRight, drawableRight),
                null
            )
        } else {
            fun getPossibleDrawable(@DrawableRes drawableRes: Int?, drawable: Drawable?): Drawable? {
                return if (drawableRes == null || drawableRes == 0) {
                    // 使用drawable
                    drawable ?: return null
                } else {
                    drawableRes.toDrawable(context) ?: return null
                }
            }

            val left = getPossibleDrawable(drawableResLeft, drawableLeft)
            val right = getPossibleDrawable(drawableResRight, drawableRight)
            tv.setDrawablesWithIntrinsicBounds(left = left, right = right)
        }

        drawableTint?.getIntRelatedColor(context)?.let {
            TextViewCompat.setCompoundDrawableTintList(tv, ColorStateList.valueOf(it))
        }

        tv.setBackground(config.background)
        return tv
    }

    private fun View.setBackground(background: IntRes?) {
        when (background) {
            is IntRes.ColorInt -> setStateListDrawable(background.value)
            is IntRes.ColorRes -> setStateListDrawable(background.value.toColor(context))
            is IntRes.DrawableRes -> setBackgroundResource(background.value)
            else -> {
                // is null, do nothing
            }
        }
    }
}

class IconAttrs(context: Context) : BaseAttrs(context) {

    private val config: AppBarConfig.Icon
        get() = Config.appBar().icon

    var drawable: IntRes? = null
    var drawableTint: IntRes? = null
    var iconSize: Int = toPx(config.size, ConstraintSet.WRAP_CONTENT)
    var paddingStart: Int = toPx(config.paddingStart)
    var paddingEnd: Int = toPx(config.paddingEnd)

    fun build(context: Context): View {
        // 创建容器
        val parent = ConstraintLayout(context)

        if (!config.adaptSquareShape) {
            // 不需要适配正方形, 则使用padding属性
            parent.setPadding(paddingStart, 0, paddingEnd, 0)
        }

        // 创建image view
        val iv = AppCompatImageView(context).apply {
            when (val res = this@IconAttrs.drawable) {
                is IntRes.ColorInt -> setImageDrawable(ColorDrawable(res.value))
                is IntRes.ColorRes -> setImageDrawable(ColorDrawable(res.value.toColor(context)))
                is IntRes.DrawableRes -> setImageResource(res.value)
                else -> {
                    // do nothing
                }
            }
            when (val res = this@IconAttrs.drawableTint) {
                is IntRes.ColorRes -> {
                    ImageViewCompat.setImageTintList(this, res.value.toColorStateList(context))
                }

                else -> {
                    // 其他类型都不处理
                }
            }

            if (iconSize <= 0) {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            ensureIdExist()
            parent.addView(this)
        }

        ConstraintSet().asStyle {
            val viewId = iv.id
            withTheme(viewId) {
                alignCenter()
            }
            constrainWidth(viewId, iconSize)
            constrainHeight(viewId, iconSize)
        }.applyToWithoutCustom(parent)

        onClick?.let {
            parent.setOnDebounceClickListener { view -> it(view) }
            parent.setBackground(config.background)
        }

        return parent
    }

    /**
     * 设置点击背景色
     */
    private fun View.setBackground(background: IntRes?) {
        when (background) {
            is IntRes.ColorInt -> setStateListDrawable(background.value)
            is IntRes.ColorRes -> setStateListDrawable(background.value.toColor(context))
            is IntRes.DrawableRes -> {
                val iconConfig = Config.appBar().icon
                val d = background.value.toDrawable(context)
                if (isSdkAtLeast(SdkInt.L_21)) {
                    if (d is RippleDrawable) {
                        // 规范水波纹的范围, 不要超出AppBar范围
                        val defDiameter = toPx(iconConfig.diameter)
                        val size = (defDiameter / sqrt(2.0)).toInt() // 内切正方形的大小

                        var start = 0
                        var top = 0
                        var right = 0
                        var bottom = 0
                        when (iconConfig.shape) {
                            AppBarConfig.Icon.Shape.SQUARE -> {
                                start = 0
                                top = 0
                                right = defDiameter
                                bottom = defDiameter
                            }

                            AppBarConfig.Icon.Shape.SQUARE_INSIDE -> {
                                val margin = (defDiameter - size) / 2
                                start = margin
                                top = margin
                                right = size + start
                                bottom = size + top
                            }

                            AppBarConfig.Icon.Shape.WRAP -> {
                                start = 0
                                top = (defDiameter - size) / 2
                                right = size + start
                                bottom = size + top
                            }
                        }
                        d.setHotspotBounds(start, top, right, bottom)
                    }
                }
                this.background = d
            }

            else -> {
                // is null, do nothing
            }
        }
    }
}