@file:Suppress("unused")

package vector.bindingadapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.ReplacementTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.doOnLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.app.ext.view.setDrawableIdsWithIntrinsicBounds
import vector.app.os.dimenRes
import vector.app.util.toColor
import vector.bindingadapter.bind.Bind
import vector.bindingadapter.bind.TextChangedBinding

enum class TransformationMethodType {
    HIDE_RETURNS,
    PASSWORD,
    REPLACEMENT,
    SINGLE_LINE
}

object TextViewBinding {

    private const val BOLD = BINDING_PREFIX + "textView_bold"
    private const val MOVEMENT_METHOD = BINDING_PREFIX + "textView_movementMethod"
    private const val TRANSFORMATION_METHOD = BINDING_PREFIX + "textView_transformationMethod"

    private const val DRAWABLE_LEFT_ID = BINDING_PREFIX + "textView_drawableLeftId"
    private const val DRAWABLE_TOP_ID = BINDING_PREFIX + "textView_drawableTopId"
    private const val DRAWABLE_RIGHT_ID = BINDING_PREFIX + "textView_drawableRightId"
    private const val DRAWABLE_BOTTOM_ID = BINDING_PREFIX + "textView_drawableBottomId"
    private const val TEXT_CHANGED = BINDING_PREFIX + "textView_textChanged"
    private const val EDITOR_ACTION = BINDING_PREFIX + "textView_editorAction"

    private const val INPUT_TYPE = BINDING_PREFIX + "inputType"
    private const val TEXT_COLOR_RES = BINDING_PREFIX + "textColorRes"

    private const val TEXT_RES = BINDING_PREFIX + "textView_resId"
    private const val TEXT_SIZE_RES = BINDING_PREFIX + "textView_textSizeRes"

    private const val TEXT_INT_NUMBER = BINDING_PREFIX + "textView_intNumber"
    private const val TEXT_FLOAT_NUMBER = BINDING_PREFIX + "textView_floatNumber"

    private const val DRAWABLE_ALIGN_START = BINDING_PREFIX + "textView_drawableAlignStart"
    private const val DRAWABLE_ALIGN_END = BINDING_PREFIX + "textView_drawableAlignEnd"

    private object Drawables {
        const val START = 0
        const val TOP = 1
        const val END = 2
        const val BOTTOM = 3
    }

    /**
     * 不干涉原来的逻辑的情况下, 只调整drawable的位置, 对图片的大小规则的统一有要求
     */
    @JvmStatic
    @BindingAdapter(DRAWABLE_ALIGN_START, DRAWABLE_ALIGN_END, requireAll = false)
    fun setDrawableAlign(textView: TextView, start: Drawable?, end: Drawable?) {
        textView.doOnLayout {
            fun setDrawableBounds(drawable: Drawable, tag: Int) {
                val drawableWidth = drawable.intrinsicWidth
                val drawableHeight = drawable.intrinsicHeight
                var left = 0
                var right = 0

                val textWidth = textView.paint.measureText(textView.text.toString())
                val drawablePadding = textView.compoundDrawablePadding

                /**
                 * 思路: 图片 + 文本组合起来考虑为一个完整的图片来计算
                 */
                // textView可绘制区域
                val canvasDrawWidth = textView.width - textView.paddingStart - textView.paddingEnd
                // 图片 + 文本 的宽度
                val drawableAndTextWidth = drawableWidth + drawablePadding + textWidth

                when (tag) {
                    Drawables.START -> {
                        left = ((canvasDrawWidth - drawableAndTextWidth) / 2).toInt()
                        right = left + drawableWidth
                    }

                    Drawables.END -> {
                        val textStart = (canvasDrawWidth - drawableAndTextWidth) / 2
                        val drawableStartAlignLeft = textStart + drawablePadding + textWidth
                        // 在准备绘制的时候, canvas已经移动到super原本要绘制drawable的地方(右对齐)
                        left = (drawableStartAlignLeft - (canvasDrawWidth - drawableWidth)).toInt()
                        right = left + drawableWidth
                    }
                }

                val top = 0
                val bottom = top + drawableHeight

                drawable.setBounds(left, top, right, bottom)
            }

            if (start != null) {
                setDrawableBounds(start, Drawables.START)
            }
            if (end != null) {
                setDrawableBounds(end, Drawables.END)
            }

            val dr = textView.compoundDrawables
            val drawableStart: Drawable? = start ?: dr[Drawables.START]
            val drawableEnd: Drawable? = end ?: dr[Drawables.END]
            textView.setCompoundDrawables(
                drawableStart,
                dr[Drawables.TOP],
                drawableEnd,
                dr[Drawables.BOTTOM]
            )
        }
    }

    @JvmStatic
    @BindingAdapter(TEXT_CHANGED)
    fun setTextChanged(textView: TextView, textChanged: Bind.Text.TextChanged) {
        val action = TextChangedBinding.Action()
        textChanged.action(action)
        textView.addTextChangedListener(
            beforeTextChanged = { s, start, count, after ->
                action.before?.invoke(s, start, count, after)
            },
            onTextChanged = { s, start, before, count ->
                action.on?.invoke(s, start, before, count)
            },
            afterTextChanged = {
                action.after?.invoke(it)
            }
        )
    }

    @JvmStatic
    @BindingAdapter(EDITOR_ACTION)
    fun setOnEditorActionListener(textView: TextView, editorAction: Bind.Text.OnEditorAction) {
        textView.setOnEditorActionListener { _, actionId, _ ->
            editorAction.action(textView, actionId)
        }
    }

    @JvmStatic
    @BindingAdapter(BOLD)
    fun setBold(textView: TextView, isBold: Boolean) {
        textView.typeface = if (isBold) {
            Typeface.defaultFromStyle(Typeface.BOLD)
        } else {
            Typeface.defaultFromStyle(Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter(MOVEMENT_METHOD)
    fun setMovementMethod(textView: TextView, method: MovementMethod) {
        textView.movementMethod = method
    }

    @JvmStatic
    @BindingAdapter(INPUT_TYPE)
    fun setInputType(textView: TextView, type: Int) {
        textView.inputType = type
    }

    /**
     * FIXME: 暂不支持[ReplacementTransformationMethod]
     */
    @JvmStatic
    @BindingAdapter(TRANSFORMATION_METHOD)
    fun setTransformationMethod(textView: TextView, type: TransformationMethodType) {
        val method = when (type) {
            TransformationMethodType.HIDE_RETURNS -> HideReturnsTransformationMethod.getInstance()
            TransformationMethodType.PASSWORD -> PasswordTransformationMethod.getInstance()
            TransformationMethodType.REPLACEMENT -> {
                object : ReplacementTransformationMethod() {
                    override fun getOriginal(): CharArray {
                        return CharArray(0)
                    }

                    override fun getReplacement(): CharArray {
                        return CharArray(0)
                    }
                }
            }

            TransformationMethodType.SINGLE_LINE -> SingleLineTransformationMethod.getInstance()
        }
        textView.transformationMethod = method
    }

    @JvmStatic
    @BindingAdapter(
        DRAWABLE_LEFT_ID, DRAWABLE_TOP_ID, DRAWABLE_RIGHT_ID, DRAWABLE_BOTTOM_ID,
        requireAll = false
    )
    fun setDrawableId(
        view: TextView,
        @DrawableRes drawableLeftId: Int?,
        @DrawableRes drawableTopId: Int?,
        @DrawableRes drawableRightId: Int?,
        @DrawableRes drawableBottomId: Int?,
    ) {
        view.setDrawableIdsWithIntrinsicBounds(
            drawableLeftId,
            drawableTopId,
            drawableRightId,
            drawableBottomId
        )
    }

    @JvmStatic
    @BindingAdapter(TEXT_COLOR_RES)
    fun setTextColor(view: TextView, @ColorRes color: Int) {
        view.setTextColor(color.toColor(view.context))
    }

    @JvmStatic
    @BindingAdapter(TEXT_RES)
    fun setText(view: TextView, @StringRes resId: Int) {
        if (resId == 0) return
        view.setText(resId)
    }

    @JvmStatic
    @BindingAdapter(TEXT_SIZE_RES)
    fun setTextDimen(view: TextView, @DimenRes resId: Int) {
        if (resId == 0) return
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, resId.dimenRes.toPx(view).toFloat())
    }

    @JvmStatic
    @BindingAdapter(TEXT_INT_NUMBER)
    fun setTextIntNumber(view: TextView, number: Int?) {
        if (number == null) return
        val old = view.text.textToInt()
        if (old == number) return
        view.text = number.toString()
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = TEXT_INT_NUMBER)
    fun getTextIntNumber(view: TextView): Int {
        return view.text.textToInt()
    }

    @JvmStatic
    @BindingAdapter(TEXT_FLOAT_NUMBER)
    fun setTextFloatNumber(view: TextView, number: Float?) {
        if (number == null) return
        val old = view.text.textToFloat()
        if (old == number) return
        view.text = number.toString()
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = TEXT_FLOAT_NUMBER)
    fun getTextFloatNumber(view: TextView): Float {
        return view.text.textToFloat()
    }

    @JvmStatic
    @BindingAdapter(
        TEXT_INT_NUMBER + ATTR_CHANGED_SUFFIX, TEXT_FLOAT_NUMBER + ATTR_CHANGED_SUFFIX,
        requireAll = false
    )
    fun setIntTextChanged(
        textView: TextView,
        intAttrChange: InverseBindingListener?,
        floatAttrChange: InverseBindingListener?,
    ) {
        textView.doAfterTextChanged {
            intAttrChange?.onChange()
            floatAttrChange?.onChange()
        }
    }

    private fun CharSequence.textToInt() = toString().toIntOrNull() ?: -1
    private fun CharSequence.textToFloat() = toString().toFloatOrNull() ?: -1f
}