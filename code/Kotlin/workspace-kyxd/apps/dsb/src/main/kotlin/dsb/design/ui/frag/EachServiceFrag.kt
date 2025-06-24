package dsb.design.ui.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import dsb.Bus
import dsb.EventId
import dsb.databinding.FragEachServiceBinding
import dsb.design.ui.itembinder.Services
import dsb.design.viewModel.EachServiceViewModel
import dsb.ext.withToast
import dsb.ext.withViewState
import dsb.model.Service
import lib.base.design.frag.BaseDBFrag
import vector.annotation.LayoutBindingClass
import vector.app.databinding.frag.DBFragEx
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020-06-23
 */
@AndroidEntryPoint
@LayoutBindingClass<FragEachServiceBinding>
class EachServiceFrag : BaseDBFrag<EachServiceViewModel>() {

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.IDLE

    val itemBinder = listOf(
        Services.ItemBinder.Header(),
        Services.ItemBinder.Data(object : Services.ItemBinder.Data.Listener {
            override fun onDetailClick(item: Service) {
            }

            override fun onProgressClick(item: Service) {
                Bus.get().send(EventId.POPUP_SERVICE_PROGRESS, item)
            }
        })
    )
    val emptyBinder = Services.ItemBinder.Empty(this)


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragEachServiceBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun flowOfSetup() {
        viewModel.fetchData().withViewState(this).withToast()
    }

    val onFundClick = Bind.OnClick {

    }

    val onDocumentClick = Bind.OnClick {

    }

    val onLawClick = Bind.OnClick {

    }

    val onInsuranceClick = Bind.OnClick {

    }
}