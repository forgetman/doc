package dsb.design.ui.itembinder

import androidx.databinding.ViewDataBinding
import dsb.BR
import dsb.databinding.LayoutCityItemSearchBinding
import dsb.databinding.LayoutInfoItemBinding
import dsb.databinding.LayoutMsgDetailItemBinding
import dsb.databinding.LayoutMsgItemBinding
import dsb.model.City
import dsb.model.DetailMessage
import dsb.model.Info
import dsb.model.InfoMessage
import vector.app.databinding.adapter.binder.DBItemBinder

/**
 * @author yuansui
 * @since 2019/1/23
 */
abstract class SimpleDBItemBinder<T, VDB : ViewDataBinding> : DBItemBinder<T, VDB>() {

    override fun onBindBinding(item: T, binding: VDB, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.owner, this)
        binding.executePendingBindings()
    }
}

class SearchCityItemBinder : SimpleDBItemBinder<City, LayoutCityItemSearchBinding>()

class MsgItemBinder : SimpleDBItemBinder<InfoMessage, LayoutMsgItemBinding>()

class MsgDetailItemBinder : SimpleDBItemBinder<DetailMessage, LayoutMsgDetailItemBinding>()

class InfoItemBinder : SimpleDBItemBinder<Info, LayoutInfoItemBinding>()