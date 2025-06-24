package dsb.binding

import androidx.databinding.BindingAdapter
import dsb.view.AutoVerticalScrollTextView
import vector.bindingadapter.BINDING_PREFIX

/**
 * @author yuansui
 * @since 2019/1/31
 */
object AutoScrollTextViewBinding {

    private const val TEXTS = BINDING_PREFIX + "autoScrollTextView_texts"
    private const val TOGGLE = BINDING_PREFIX + "autoScrollTextView_toggle"

    @JvmStatic
    @BindingAdapter(TEXTS, TOGGLE, requireAll = false)
    fun setTexts(view: AutoVerticalScrollTextView, texts: Array<String>?, toggle: Boolean?) {
        view.setTexts(texts)
        if (toggle == true) view.start() else view.stop()
    }
}