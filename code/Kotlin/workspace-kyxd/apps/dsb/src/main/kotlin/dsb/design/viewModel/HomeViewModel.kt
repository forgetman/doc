package dsb.design.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.Bus
import dsb.EventId
import dsb.design.repo.FormRepo
import dsb.design.repo.MessageRepo
import dsb.ext.checkSignIn
import dsb.ext.withWebParams
import dsb.model.HomeMessage
import eth.ext.bind
import eth.model.Nive
import lib.base.model.Form
import lib.base.model.Page
import live.Live
import live.ext.get
import vector.app.viewmodel.ViewModelEx
import vector.bindingadapter.bind.Bind
import vector.swiperefresh.widget.LoadMore
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/21
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val formRepo: FormRepo,
    private val messageRepo: MessageRepo
) : ViewModelEx() {

    val forms = Nive<List<Form>>()
    val message = Nive<HomeMessage>()

    val gpsName = Live<String>()

    private var page = Page()

    fun fetchMessage() =
        messageRepo.fetchHomeMessage().bind(message).launch(viewModelScope)

    fun fetchForms(cityId: String?, state: LoadMore.State? = null) =
        formRepo.fetchData(cityId, page.change(state))
            .bind(forms, page.refresh())
            .launch(viewModelScope)

    val onItemClick = ScrollableBind.List.OnItemClick {
        // todo: 需要处理 24/25/36
        val item = forms[it] ?: return@OnItemClick
        if (item.needLogin && !checkSignIn()) return@OnItemClick
        Bus.get().send(EventId.LAUNCH_WEB, item.url?.withWebParams())
    }
}