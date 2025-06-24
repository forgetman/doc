package dsb.design.ui.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.Bus
import dsb.EventId
import dsb.databinding.FragServiceBinding
import dsb.design.ui.dialog.ServiceProgressDialog
import dsb.model.Service
import lib.base.design.frag.BaseSimpleDBFrag
import live.Live
import vector.app.adapter.pager.fragPagerListOf
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020-06-22
 */
class ServiceFrag : BaseSimpleDBFrag() {

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.RESUME

    val pager = fragPagerListOf {
        add { EachServiceFrag() }
        add { EachServiceFrag() }
    }

    val currItem = Live<Int>()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragServiceBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("我的服务")
    }

    override fun flowOfSetup() {
        Bus.get().with(this).onValue<Service>(EventId.POPUP_SERVICE_PROGRESS) {
            val dialog = ServiceProgressDialog(context)
            dialog.show()
        }
    }

    val onServingClick = Bind.OnClick {
        currItem.value = 0
    }

    val onServCompleteClick = Bind.OnClick {
        currItem.value = 1
    }
}