package fund.design.ui.adapter

import androidx.databinding.ViewDataBinding
import fund.BR
import fund.R
import fund.model.Me
import fund.model.MeLayoutStyle
import vector.design.ui.adapter.data.MultiAdapterEx

/**
 * @author yuansui
 * @since 2019/1/21
 */
class MeAdapter : MultiAdapterEx<Me>() {

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            MeLayoutStyle.TEXT.ordinal -> R.layout.layout_me_item_type_text
            MeLayoutStyle.LOGOUT.ordinal -> R.layout.layout_me_item_type_logout
            else -> R.layout.layout_me_item_type_divider
        }
    }

    override fun getViewType(position: Int): Int {
        val item = data?.get(position) ?: return MeLayoutStyle.TEXT.ordinal
        return item.style.ordinal
    }

    override fun onBindBinding(viewType: Int, item: Me, binding: ViewDataBinding) {
        binding.setVariable(BR.item, item)
    }
}