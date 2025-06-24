package fund.design.viewModel

import fund.design.repo.HomeRepo
import fund.ext.toWebWithoutLogin
import lib.base.model.Page
import vector.bindingadapter.onBind.Bind
import vector.design.ui.plugin.LoadMore
import vector.design.viewModel.ViewModelEx

/**
 * @author yuansui
 * @since 2018/7/28 0028
 */
class HomeViewModel : ViewModelEx() {

    private val repo = HomeRepo()
    private var page = Page()

    val data = repo.data

    fun query(state: LoadMore.State? = null) =
        repo.query(page.change(state))

    val onItemClick = ScrollableBind.List.OnItemClick { view, position ->
        val item = data.value?.get(position) ?: return@onItemClick
        item.url.toWebWithoutLogin()
    }
}