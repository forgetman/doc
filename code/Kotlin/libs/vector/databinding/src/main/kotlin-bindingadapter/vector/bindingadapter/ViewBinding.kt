@file:Suppress("unused")

package vector.bindingadapter

import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter
import vector.app.ext.view.doOnApplyWindowInsets
import vector.app.ext.view.gone
import vector.app.ext.view.margin
import vector.app.ext.view.onLongClick
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.ext.view.setOnMultiClickListener
import vector.app.ext.view.show
import vector.app.os.dimenRes
import vector.app.util.toColor
import vector.app.util.toDrawable
import vector.bindingadapter.bind.Bind
import vector.bindingadapter.bind.MultiClickAction
import vector.ext.systemBottom
import vector.ext.systemLeft
import vector.ext.systemRight
import vector.ext.systemTop


object ViewBinding {

    private const val REQUEST_FOCUS = BINDING_PREFIX + "view_requestFocus"
    private const val ON_CLICK = BINDING_PREFIX + "view_onClick"
    private const val ON_DEBOUNCE_CLICK = BINDING_PREFIX + "view_onDebounceClick"
    private const val ON_LONG_CLICK = BINDING_PREFIX + "view_onLongClick"
    private const val ON_MULTI_CLICK = BINDING_PREFIX + "view_onMultiClick"
    private const val ON_FOCUS_CHANGED = BINDING_PREFIX + "view_onFocusChanged"
    private const val ON_TOUCH = BINDING_PREFIX + "view_onTouch"
    private const val REQUEST_SELECTED = BINDING_PREFIX + "view_isSelected"
    private const val LAYOUT_WIDTH = BINDING_PREFIX + "view_layoutWidth"
    private const val LAYOUT_HEIGHT = BINDING_PREFIX + "view_layoutHeight"
    private const val ON_LAYOUT = BINDING_PREFIX + "view_onLayout"
    private const val VISIBLE = BINDING_PREFIX + "view_visible"

    private const val LAYOUT_WIDTH_RES = BINDING_PREFIX + "view_layoutWidthRes"
    private const val LAYOUT_HEIGHT_RES = BINDING_PREFIX + "view_layoutHeightRes"

    private const val LAYOUT_MARGIN_TOP_RES = BINDING_PREFIX + "view_layoutMarginTopRes"
    private const val LAYOUT_MARGIN_BOTTOM_RES = BINDING_PREFIX + "view_layoutMarginBottomRes"
    private const val LAYOUT_MARGIN_START_RES = BINDING_PREFIX + "view_layoutMarginStartRes"
    private const val LAYOUT_MARGIN_END_RES = BINDING_PREFIX + "view_layoutMarginEndRes"
    private const val LAYOUT_MARGIN_RES = BINDING_PREFIX + "view_layoutMarginRes"

    /**
     * 拓展原生已有属性
     */
    private const val COLOR_RES = BINDING_PREFIX + "backgroundColor"
    private const val COLOR_INT = BINDING_PREFIX + "backgroundColorInt"
    private const val BACKGROUND_RES = BINDING_PREFIX + "backgroundRes"

    private const val LAYOUT_MARGIN_TOP = BINDING_PREFIX + "layout_marginTop"
    private const val LAYOUT_MARGIN_BOTTOM = BINDING_PREFIX + "layout_marginBottom"
    private const val LAYOUT_MARGIN_START = BINDING_PREFIX + "layout_marginStart"
    private const val LAYOUT_MARGIN_END = BINDING_PREFIX + "layout_marginEnd"
    private const val LAYOUT_MARGIN = BINDING_PREFIX + "layout_margin"

    private const val PADDING_SYSTEM_WINDOW_START =
        BINDING_PREFIX + "view_paddingStartSystemWindowInsets"
    private const val PADDING_SYSTEM_WINDOW_TOP =
        BINDING_PREFIX + "view_paddingTopSystemWindowInsets"
    private const val PADDING_SYSTEM_WINDOW_END =
        BINDING_PREFIX + "view_paddingEndSystemWindowInsets"
    private const val PADDING_SYSTEM_WINDOW_BOTTOM =
        BINDING_PREFIX + "view_paddingBottomSystemWindowInsets"

    @JvmStatic
    @BindingAdapter(
        PADDING_SYSTEM_WINDOW_START,
        PADDING_SYSTEM_WINDOW_TOP,
        PADDING_SYSTEM_WINDOW_END,
        PADDING_SYSTEM_WINDOW_BOTTOM,
        requireAll = false
    )
    fun applySystemWindows(
        view: View,
        applyStart: Int?,
        applyTop: Int?,
        applyEnd: Int?,
        applyBottom: Int?
    ) {
        view.doOnApplyWindowInsets { v, insets, initialPadding ->

            fun adapt(value: Int?, systemValue: Int): Int =
                if (value != null) (value + systemValue) else 0

            val start = adapt(applyStart, insets.systemLeft)
            val top = adapt(applyTop, insets.systemTop)
            val end = adapt(applyEnd, insets.systemRight)
            val bottom = adapt(applyBottom, insets.systemBottom)

            v.updatePadding(
                initialPadding.start + start,
                initialPadding.top + top,
                initialPadding.end + end,
                initialPadding.bottom + bottom
            )

            insets
        }
    }

    @JvmStatic
    @BindingAdapter(REQUEST_SELECTED)
    fun setRequestSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }

    @JvmStatic
    @BindingAdapter(VISIBLE)
    fun setVisible(view: View, visible: Boolean) {
        if (visible) view.show() else view.gone()
    }

    @JvmStatic
    @BindingAdapter(REQUEST_FOCUS)
    fun setRequestFocus(view: View, focus: Boolean) {
        when (focus) {
            true -> {
                view.isFocusable = true
                view.isFocusableInTouchMode = true
                view.requestFocus()
            }

            false -> view.clearFocus()
        }
    }

    @JvmStatic
    @BindingAdapter(ON_CLICK)
    fun setOnClick(view: View, binding: Bind.OnClick) {
        view.setOnClickListener {
            binding.action(it)
        }
    }

    @JvmStatic
    @BindingAdapter(ON_DEBOUNCE_CLICK)
    fun setOnDebounceClick(view: View, binding: Bind.OnDebounceClick) {
        view.setOnDebounceClickListener(binding.interval) {
            binding.action(it)
        }
    }

    @JvmStatic
    @BindingAdapter(ON_LONG_CLICK)
    fun setOnLongClick(view: View, binding: Bind.OnLongClick) {
        view.onLongClick {
            binding.action(it)
        }
    }

    @JvmStatic
    @BindingAdapter(ON_MULTI_CLICK)
    fun setMultiClick(view: View, binding: Bind.OnMultiClick) {
        val action = MultiClickAction()
        binding.action.invoke(action)
        view.setOnMultiClickListener(action.onSingleClick, action.onDoubleClick, action.onLongClick)
    }

    @JvmStatic
    @BindingAdapter(ON_FOCUS_CHANGED)
    fun setOnFocusChanged(view: View, binding: Bind.OnFocusChanged) {
        view.setOnFocusChangeListener { v, hasFocus -> binding.action(v, hasFocus) }
    }

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    @BindingAdapter(ON_TOUCH)
    fun setOnTouch(view: View, binding: Bind.OnTouch) {
        view.setOnTouchListener { v, event ->
            binding.action(v, event)
        }
    }

    @JvmStatic
    @BindingAdapter(COLOR_INT)
    fun setBackgroundColorInt(view: View, @ColorInt color: Int) {
        view.setBackgroundColor(color)
    }

    @JvmStatic
    @BindingAdapter(
        LAYOUT_MARGIN_START,
        LAYOUT_MARGIN_TOP,
        LAYOUT_MARGIN_END,
        LAYOUT_MARGIN_BOTTOM,
        requireAll = false
    )
    fun setLayoutMargins(view: View, start: Int?, top: Int?, end: Int?, bottom: Int?) {
        view.margin(start = start, top = top, end = end, bottom = bottom)
    }

    @JvmStatic
    @BindingAdapter(COLOR_RES)
    fun setBackgroundColor(view: View, @ColorRes id: Int) {
        view.setBackgroundColor(id.toColor(view.context))
    }

    @JvmStatic
    @BindingAdapter(BACKGROUND_RES)
    fun setBackgroundInt(view: View, @DrawableRes id: Int) {
        view.background = id.toDrawable(view.context)
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_MARGIN)
    fun setLayoutMargin(view: View, margin: Int) {
        view.margin(margin, margin, margin, margin)
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_WIDTH)
    fun setWidth(view: View, width: Int) {
        view.layoutParams.width = width
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_HEIGHT)
    fun setHeight(view: View, height: Int) {
        view.layoutParams.height = height
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_WIDTH_RES)
    fun setWidthRes(view: View, @DimenRes resId: Int) {
        if (resId == 0) return
        view.layoutParams.width = resId.dimenRes.toPx(view)
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_HEIGHT_RES)
    fun setHeightRes(view: View, @DimenRes resId: Int) {
        if (resId == 0) return
        view.layoutParams.height = resId.dimenRes.toPx(view)
    }

    @JvmStatic
    @BindingAdapter(
        LAYOUT_MARGIN_START_RES,
        LAYOUT_MARGIN_TOP_RES,
        LAYOUT_MARGIN_END_RES,
        LAYOUT_MARGIN_BOTTOM_RES,
        requireAll = false
    )
    fun setLayoutMarginsRes(
        view: View,
        @DimenRes startId: Int?,
        @DimenRes topId: Int?,
        @DimenRes endId: Int?,
        @DimenRes bottomId: Int?
    ) {
        val start = startId?.dimenRes?.toPx(view)
        val top = topId?.dimenRes?.toPx(view)
        val end = endId?.dimenRes?.toPx(view)
        val bottom = bottomId?.dimenRes?.toPx(view)
        view.margin(start = start, top = top, end = end, bottom = bottom)
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_MARGIN_RES)
    fun setLayoutMarginRes(view: View, @DimenRes marginId: Int) {
        val margin = marginId.dimenRes.toPx(view)
        view.margin(margin, margin, margin, margin)
    }

    @JvmStatic
    @BindingAdapter(ON_LAYOUT)
    fun doOnLayout(view: View, binding: Bind.OnLayout) {
        view.doOnLayout {
            binding.action(it)
        }
    }
}

