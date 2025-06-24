package dsb.design.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.InfoRepo
import dsb.model.Info
import eth.ext.bind
import eth.model.Nive
import lib.base.model.Page
import vector.app.viewmodel.ViewModelEx
import vector.swiperefresh.widget.LoadMore
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/17
 */
@HiltViewModel
class EachInfoViewModel @Inject constructor(private val repo: InfoRepo) : ViewModelEx() {

    val infos = Nive<List<Info>>()

    private var page = Page()

    var id: Int = 0

    fun fetchInfo(state: LoadMore.State? = null) =
        repo.fetchInfo(id, page.change(state))
            .bind(infos, page.refresh())
            .launch(viewModelScope)
}