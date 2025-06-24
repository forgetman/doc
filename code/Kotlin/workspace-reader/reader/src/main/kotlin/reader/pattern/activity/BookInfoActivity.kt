package reader.pattern.activity

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import live.Live
import logger.L
import reader.Bus
import reader.EventId
import reader.databinding.ActivityBookInfoBinding
import reader.db.Db
import reader.ext.addBackIcon
import reader.ext.withToast
import reader.ext.withViewState
import reader.model.Book
import reader.network.api.InfoApi
import reader.network.createApi
import reader.pattern.adapter.BookInfoAdapter
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.databinding.annotation.LayoutBindingClass
import vector.bindingadapter.bind.Bind
import vector.ext.toast
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.scrollable.decoration.Decoration
import vector.widget.scrollable.layoutmanager.LayoutManagers

/**
 * @author yuansui
 * @since 2018/9/7
 */
@Creator
@LayoutBindingClass<ActivityBookInfoBinding>
class BookInfoActivity : SimpleDBActivityEx() {

    companion object {
        private const val LOG_TAG = "BookInfoActivity"
    }

    enum class From {
        SEARCH,
        CHECK
    }

    @Extra
    lateinit var bookId: String

    @Extra
    lateinit var from: From

    val itemBinders = listOf(
        BookInfoAdapter.Binder.Header(),
        BookInfoAdapter.Binder.More()
    )
    val decoration by lazy(LazyThreadSafetyMode.NONE) {
        Decoration.grid {
            drawTop = false
            drawBottom = false
            color = Color.TRANSPARENT
        }
    }
    val manager = LayoutManagers.grid(3)

    private val book = MutableStateFlow<Book?>(null)
    val data: StateFlow<List<BookInfoAdapter.Data>> = book.filterNotNull().map {
        buildList<BookInfoAdapter.Data> {
            add(BookInfoAdapter.Header(it))
            it.sameUserBooks?.map { same ->
                BookInfoAdapter.More(same)
            }?.let { more ->
                addAll(more)
            }
        }
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), emptyList())

    val optionVisible = Live<Boolean>()
    val addEnable = Live(true)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityBookInfoBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText { text = "详情" }

        when (from) {
            From.SEARCH -> optionVisible.value = true
            From.CHECK -> optionVisible.value = false
        }
    }

    override fun initializeContentView() {
        getData()

        val sameBook = Db.sync { hasBook(bookId) }
        if (sameBook) addEnable.value = false

        Bus.getInstance().with(this).onMessage(EventId.FINISH_ADD_BOOK) {
            finish()
        }
    }

    val onClick = Bind.OnClick {
        val item = book.value ?: return@OnClick
        item.readTime = System.currentTimeMillis()

        val oldBook = Db.sync { getBook(item.id) }
        if (oldBook != null) {
            ReadActivityCreator.create(oldBook).start(this)
            Db.async { update(item) }
            Bus.getInstance().send(EventId.UPDATE_BOOK_READ_TIME, item)
        } else {
            ReadActivityCreator.create(item).start(this)
            Db.async { insert(item) }
            Bus.getInstance().send(EventId.ADD_BOOK, item)
        }

        Bus.getInstance().send(EventId.FINISH_ADD_BOOK)
    }

    val onAddClick = Bind.OnClick {
        val item = book.value ?: return@OnClick
        item.readTime = System.currentTimeMillis()

        val oldBook = Db.sync { getBook(item.id) }
        if (oldBook == null) {
            Db.async { insert(item) }
            addEnable.value = false
            toast("已加入书架")

            Bus.getInstance().send(EventId.ADD_BOOK, item)
        }
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        if (position == 0) return@OnItemClick // header不响应s点击
        val item = data.value.getOrNull(position) as? BookInfoAdapter.More ?: return@OnItemClick
        BookInfoActivityCreator.create(item.book.id, From.SEARCH).start(this)
    }

    override fun onRetryClick() {
        getData()
    }

    private fun getData() {
        createApi<InfoApi>()
            .info(bookId)
            .withViewState(this)
            .withToast()
            .onEach {
                book.value = it
            }.catch { e ->
                L.e(LOG_TAG, "getData", e)
            }.launchIn(this)
    }
}