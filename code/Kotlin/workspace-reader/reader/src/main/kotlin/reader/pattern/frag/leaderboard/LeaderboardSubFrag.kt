package reader.pattern.frag.leaderboard

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import reader.R
import reader.databinding.FragLeaderboardSubBinding
import reader.ext.catch
import reader.ext.fixLoadMoreVisibility
import reader.ext.withLoadMore
import reader.ext.withSwipeState
import reader.ext.withToast
import reader.ext.withViewState
import reader.model.Book
import reader.model.Page
import reader.network.api.Category
import reader.network.api.LeaderboardType
import reader.pattern.activity.BookInfoActivity
import reader.pattern.activity.BookInfoActivityCreator
import reader.pattern.adapter.LeaderboardItemItemBinder
import reader.pattern.repo.BookRepo
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.os.dp
import vector.app.util.toColor
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.databinding.scrollable.binding.trigger.ScrollableBindTrigger
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.scrollable.decoration.Decoration
import vector.widget.swiperefresh.delegate.LoadMore
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2021/4/8
 * 排行榜单榜
 */
@Creator
@AndroidEntryPoint
@LayoutBindingClass<FragLeaderboardSubBinding>
class LeaderboardSubFrag : SimpleDBFragEx() {

    companion object {
        private const val LOG_TAG = "LeaderboardSubFrag"
    }

    @Extra
    lateinit var category: Category

    @Extra
    lateinit var type: LeaderboardType

    @Inject
    lateinit var repo: BookRepo

    val data = MutableStateFlow<List<Book>>(emptyList())
    val itemBinder = LeaderboardItemItemBinder()
    val decoration by lazy {
        Decoration.linear {
            val margin = 8.dp.toPx(this@LeaderboardSubFrag)

            drawTop = false
            drawBottom = false

            marginStart = margin
            marginEnd = margin

            color = R.color.divider.toColor(context)
            size = 0.5f.dp.toPx(context).toInt()
        }
    }

    val page = Page()
    val trigger = ScrollableBindTrigger.scrollToTop()

    val onSwipe = RefreshBind.OnSwipe {
        fetchLeaderboard().withSwipeState(it).catch(LOG_TAG, "refreshData").launchIn(this)
    }

    val onLoadMore = RefreshBind.OnLoadMore { delegate, lastState ->
        fetchLeaderboard(lastState).withLoadMore(delegate).catch(LOG_TAG, "refreshData")
            .launchIn(this)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = data.value.getOrNull(position) ?: return@OnItemClick
        BookInfoActivityCreator.create(item.id, BookInfoActivity.From.SEARCH).start(context)
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragLeaderboardSubBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    fun changeType(type: LeaderboardType) {
        this.type = type
        fetchLeaderboard().withViewState(this).fixLoadMoreVisibility(this).onEach {
            trigger.trig()
        }.catch(LOG_TAG, "refreshData").launchIn(this)
    }

    override fun onRetryClick() {
        fetchLeaderboard().withViewState(this).catch(LOG_TAG, "refreshData").launchIn(this)
    }

    private fun fetchLeaderboard(lastState: LoadMore.State? = null) =
        repo.fetchLeaderboard(category, type, page.change(lastState))
            .onEach {
                if (page.refresh()) {
                    data.value = it
                } else {
                    data.value += it
                }
            }
            .withToast()
}