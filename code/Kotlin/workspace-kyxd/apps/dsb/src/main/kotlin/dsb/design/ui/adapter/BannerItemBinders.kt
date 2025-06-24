package dsb.design.ui.adapter

import androidx.databinding.ViewDataBinding
import dsb.BR
import dsb.Bus
import dsb.EventId
import dsb.databinding.LayoutForm30Binding
import dsb.databinding.LayoutItem31Binding
import dsb.databinding.LayoutItem40Binding
import dsb.ext.checkSignIn
import dsb.ext.withWebParams
import dsb.model.Banner
import vector.app.databinding.adapter.pager.viewpager.binder.DBItemPagerBinder


abstract class BaseBannerItemBinder<T, VDB : ViewDataBinding> : DBItemPagerBinder<T, VDB>() {

    interface Listener {
        fun onImageClick(item: Banner)
    }

    private val listener = object : Listener {

        override fun onImageClick(item: Banner) {
            if (item.needLogin && !checkSignIn()) return
            Bus.get().send(EventId.LAUNCH_WEB, item.url?.withWebParams())
        }
    }

    final override fun onBindBinding(item: T, binding: VDB) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.listener, listener)
        binding.executePendingBindings()
    }
}

class Banner30 : Banner()
class Banner31 : Banner()
class Banner40 : Banner()

class Banner30ItemBinder : BaseBannerItemBinder<Banner30, LayoutForm30Binding>()
class Banner31ItemBinder : BaseBannerItemBinder<Banner31, LayoutItem31Binding>()
class Banner40ItemBinder : BaseBannerItemBinder<Banner40, LayoutItem40Binding>()