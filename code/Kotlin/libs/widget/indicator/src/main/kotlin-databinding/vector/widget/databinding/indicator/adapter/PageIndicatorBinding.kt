package vector.widget.databinding.indicator.adapter

import androidx.databinding.BindingAdapter
import vector.app.os.dp
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.indicator.page.UnderlinePageIndicator

object PageIndicatorBinding {
    private const val LINE_WIDTH = BINDING_PREFIX + "underlinePageIndicator_lineWidth"

    @JvmStatic
    @BindingAdapter(LINE_WIDTH)
    fun setLineWidth(view: UnderlinePageIndicator, width: Int) {
        view.setLineWidth(width.dp.toPx(view.context))
    }
}