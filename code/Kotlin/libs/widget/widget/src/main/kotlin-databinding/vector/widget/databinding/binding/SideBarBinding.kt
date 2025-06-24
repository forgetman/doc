package vector.widget.databinding.binding

import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import vector.app.os.dp
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.SideBar
import vector.widget.databinding.SideBarBind

/**
 * @author yuansui
 * @since 2019/1/22
 */
object SideBarBinding {

    private const val TEXT_SIZE = BINDING_PREFIX + "sideBar_textSize"
    private const val TEXT_GAP = BINDING_PREFIX + "sideBar_textGap"

    private const val TEXT_COLOR = BINDING_PREFIX + "sideBar_textColor"
    private const val TEXT_COLOR_FOCUS = BINDING_PREFIX + "sideBar_textColorFocus"

    private const val SELECTIONS = BINDING_PREFIX + "sideBar_selections"

    private const val ON_TOUCH_LETTER = BINDING_PREFIX + "sideBar_onTouchLetter"

    /**
     * 一起设置, 保证计算准确
     */
    @JvmStatic
    @BindingAdapter(
        TEXT_SIZE, TEXT_GAP, TEXT_COLOR, TEXT_COLOR_FOCUS, SELECTIONS,
        requireAll = false
    )
    fun setAttrs(
        view: SideBar,
        size: Int?,
        gap: Int?,
        @ColorInt color: Int?,
        @ColorInt colorFocus: Int?,
        selections: Array<String>?
    ) {
        size?.let { view.textSize = it.dp.toPx(view.context) }
        gap?.let { view.textGap = it.dp.toPx(view.context) }
        color?.let { view.color = it }
        colorFocus?.let { view.colorFocus = it }
        selections?.let { view.selections = it }
    }

    @JvmStatic
    @BindingAdapter(ON_TOUCH_LETTER)
    fun setOnTouchLetter(view: SideBar, binder: SideBarBind.OnTouchLetter) {
        view.setOnTouchLetterChangeListener(binder.action)
    }
}