package reader.pattern.viewModel

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import live.Live
import live.ext.get
import reader.model.Book
import reader.model.Page
import reader.pattern.repo.SearchRepo
import vector.app.viewmodel.ViewModelEx
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.swiperefresh.delegate.LoadMore
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2018/5/31
 */
@HiltViewModel
class SearchResultViewModel @Inject constructor(private val repo: SearchRepo, app: Application) : ViewModelEx(app) {

    val hint = "斗罗大陆"

    var keyword = Live<String?>()
    val onItemSelected = Live<Book>()
    val onHotItemSelected = Live<String>()

    val showClear = Live<Boolean>()

    val books = MutableStateFlow<List<Book>>(emptyList())
    val hots = Live<List<String>>()

    val searchRequest = MutableStateFlow<List<Book>?>(null)

    val showResult = Live(false)

    val page = Page()

    override fun onCreate() {
        hots.value = repo.fetchHots()
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = books.value.getOrNull(position) ?: return@OnItemClick
        onItemSelected.value = item
    }

    val onHotItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = hots[position] ?: return@OnItemClick
        onHotItemSelected.value = item
        onKeywordChanged(item)
    }

    fun doSearch(key: String? = keyword.value, state: LoadMore.State? = null): Flow<List<Book>> {
        showResult.value = true
        val realKey = if (!key.isNullOrEmpty()) key else hint
        onKeywordChanged(realKey)
        return repo.search(realKey, page.change(state))
            .onEach {
                if (page.refresh()) {
                    books.value = it
                } else {
                    books.value += it
                }

                searchRequest.value = it
            }
    }

    fun onKeywordChanged(keyword: String?) {
        this.keyword.value = keyword

        if (keyword.isNullOrEmpty()) {
            showResult.value = false
            showClear.value = false
            books.value = emptyList()
        } else {
            showClear.value = true
        }
    }
}