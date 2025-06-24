package reader.pattern.activity

import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import logger.L
import reader.Bus
import reader.EventId
import reader.databinding.ActivitySearchResultBinding
import reader.databinding.LayoutSearchBarBinding
import reader.ext.addBackIcon
import reader.ext.withSwipeState
import reader.ext.withToast
import reader.ext.withViewState
import reader.pattern.adapter.SearchHotItemItemBinder
import reader.pattern.adapter.SearchItemItemBinder
import reader.pattern.viewModel.SearchResultViewModel
import vector.app.databinding.activity.DBActivityEx
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.decor.ViewState
import vector.app.ext.view.hideSoftInput
import vector.bindingadapter.bind.Bind
import vector.util.MATCH_PARENT
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.scrollable.layoutmanager.LayoutManagers

/**
 * @author yuansui
 * @since 2018/5/31
 */
@AndroidEntryPoint
@LayoutBindingClass<ActivitySearchResultBinding>
class SearchActivity : DBActivityEx<SearchResultViewModel>() {

    companion object {
        private const val LOG_TAG = "SearchActivity"
    }

    val itemBinder = SearchItemItemBinder()

    val hotItemBinder = SearchHotItemItemBinder()
    val hotLayoutManager = LayoutManagers.flexbox()

    private var searchJob: Job? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivitySearchResultBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)

        val binding = LayoutSearchBarBinding.inflate(layoutInflater)
        binding.owner = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        appBar.midAlign.add {
            view = binding.root
            width = MATCH_PARENT
            height = MATCH_PARENT
        }

        appBar.right.addText {
            text = "搜索"
            onClick = {
                launchSearchJob()
            }
        }
    }

    override fun initializeContentView() {
        viewModel.onItemSelected.observe(this) {
            BookInfoActivityCreator.create(it.id, BookInfoActivity.From.SEARCH).start(this)
        }

        viewModel.onHotItemSelected.observe(this) {
            searchJob = viewModel.doSearch(it)
                .withViewState(this)
                .withToast()
                .catch { e ->
                    L.e(LOG_TAG, "initializeSystemBar", e)
                }.launchIn(this@SearchActivity)
        }

        Bus.getInstance().with(this).onMessage(EventId.FINISH_ADD_BOOK) {
            finish()
        }
    }

    val onSwipe = RefreshBind.OnSwipe {
        searchJob = viewModel.doSearch()
            .withSwipeState(it)
            .withToast()
            .catch { e ->
                L.e(LOG_TAG, "initializeSystemBar", e)
            }.launchIn(this@SearchActivity)
    }

    val onClearClick = Bind.OnClick {
        viewModel.onKeywordChanged(null)
        searchJob?.cancel()
        viewState = ViewState.NORMAL
    }

    val onEditorAction = Bind.Text.OnEditorAction { v, actionId ->
        if (actionId == EditorInfo.IME_ACTION_DONE
            || actionId == EditorInfo.IME_ACTION_SEARCH
        ) {
            v.hideSoftInput()
            launchSearchJob()
            return@OnEditorAction true
        }
        false
    }

    override fun enableHideKeyboardWhenFocusChanged(): Boolean {
        return true
    }

    private fun launchSearchJob() {
        searchJob = viewModel.doSearch()
            .withViewState(this@SearchActivity)
            .withToast()
            .catch { e ->
                L.e(LOG_TAG, "initializeSystemBar", e)
            }.launchIn(this@SearchActivity)
    }
}