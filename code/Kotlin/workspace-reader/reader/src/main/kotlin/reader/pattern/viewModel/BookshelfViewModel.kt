package reader.pattern.viewModel

import android.app.Application
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import live.Live
import live.ext.forEach
import live.ext.remove
import live.refresh
import logger.L
import reader.datastore.Settings
import reader.db.Db
import reader.model.Book
import reader.model.Chapters
import reader.model.pack.unpack
import reader.network.api.CommonApi
import reader.network.createApi
import reader.pattern.adapter.BookshelfAdapter
import reader.pattern.frag.main.BookshelfFrag
import reader.pattern.frag.main.ShelfLayoutStyle
import sugar.ext.runOnMainThread
import vector.app.os.dp
import vector.app.viewmodel.ViewModelEx
import vector.datastore.preference.asEnumFlow
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.databinding.scrollable.binding.trigger.ScrollableBindTrigger
import vector.widget.scrollable.layoutmanager.LayoutManagers
import java.util.concurrent.TimeUnit

/**
 * @author yuansui
 * @since 2018/4/2
 */
class BookshelfViewModel(app: Application) : ViewModelEx(app) {

    companion object {
        private const val LOG_TAG = "BookshelfViewModel"
    }

    val onBookSelected = MutableStateFlow<Book?>(null)
    val decoration = Live<RecyclerView.ItemDecoration?>()
    val manager = Live<LayoutManagers.LayoutManagerFactory>()
    val trigger = ScrollableBindTrigger.scrollToTop()

    lateinit var currAdapterStyle: ShelfLayoutStyle

    private val origin = Live<MutableList<Book>?>()
    val data = MediatorLiveData<List<BookshelfAdapter.Data>>().apply {
        addSource(origin) {
            if (it.isNullOrEmpty()) {
                value = emptyList()
                return@addSource
            }

            val list = mutableListOf<BookshelfAdapter.Data>()
            val header = BookshelfAdapter.Header(it[0])
            list.add(header)

            if (it.size > 1) {
                val others = it.subList(1, it.size).map { book ->
                    when (currAdapterStyle) {
                        ShelfLayoutStyle.GRID -> BookshelfAdapter.Grid(book)
                        ShelfLayoutStyle.LINEAR -> BookshelfAdapter.Linear(book)
                    }
                }
                list.addAll(others)
            }

            value = list
        }
    }

    private val books: MutableList<Book>?
        get() = origin.value

    val option = Live<Book>()

    override fun onCreate() {
        Settings.shelfStyle.asEnumFlow<ShelfLayoutStyle>()
            .filterNotNull()
            .distinctUntilChanged().onEach {
                adaptShelfStyle(it)
            }.launchIn(viewModelScope)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = books?.getOrNull(position) ?: return@OnItemClick
        item.readTime = System.currentTimeMillis()
        onBookSelected.value = item

        runOnMainThread(1, TimeUnit.SECONDS) {
            // 不使用diffUtil之后, sort的响应速度大大增加, 会在跳转之前就看到listview更改后的样式
            // 加一个延迟把列表更新延后, 以便达到更好的体验
            sort()
        }
        Db.async { update(item) }
    }

    val onItemLongClick = ScrollableBind.List.OnItemLongClick { _, position ->
        val item = books?.get(position) ?: return@OnItemLongClick
        option.value = item
    }

    fun updateReadTime(book: Book) {
        findBook(book.id) {
            this.readTime = book.readTime
            sort()
        }
    }

    fun delete(book: Book) {
        origin.remove(book)

        Db.async {
            delete(book)
            deleteBookCache(book.id)
            deleteChapters(book.id)
        }
    }

    fun addNewBook(book: Book) {
        books?.add(book)
        sort()
    }

    fun updateBookChapter(bookId: String?, number: Int?, lastName: String?) {
        findBook(bookId) {
            number?.let { chapterNum = it }
            lastName?.let { newChapterDesc = it }
        }
    }

    fun updateBookReadIndex(bookId: String?, index: Int?) {
        findBook(bookId) {
            this.index = index ?: return@findBook
        }
    }

    fun updateBook(bookId: String) {
        findBook(bookId) {
            updateChapters(this)
        }
    }

    fun updateAllBooks() {
        origin.forEach {
            updateChapters(it)
        }
    }

    private fun updateChapters(book: Book) {
        createApi<CommonApi>()
            .chapters(book.id)
            .unpack()
            .flowOn(Dispatchers.IO)
            .onStart {
                book.ui.updateState.value = true
            }.onCompletion {
                book.ui.updateState.value = false
            }.onEach {
                val size = it.size
                val lastChapterName = it.last().name

                book.newChapterDesc = lastChapterName

                val oldSize = book.chapterNum
                if (oldSize != size) {
                    book.chapterNum = size
                    if (oldSize > 0) {
                        // 初始化过后的再次更新才会展示更新图标
                        book.ui.hasNewChapters.value = true
                    }
                }

                // 更新Db
                Db.async {
                    if (oldSize != size) {
                        it.forEach { c ->
                            c.bookId = book.id
                        }
                        insert(Chapters(book.id, it))
                    }
                    updateBookChapter(book.id, size, lastChapterName)
                }

            }.catch { e ->
                L.e(LOG_TAG, "updateChapters", e)
            }.launchIn(viewModelScope)
    }

    fun getAll() {
        origin.value = Db.sync { getBooksList() }
    }

    private fun sort() {
        origin.value = books?.sortedByDescending { it.readTime }?.toMutableList()
    }

    private fun findBook(bookId: String?, action: Book.() -> Unit) {
        val item = books?.find { it.id == bookId } ?: return
        action(item)
    }

    fun adaptShelfStyle(style: ShelfLayoutStyle) {
        currAdapterStyle = style
        when (style) {
            ShelfLayoutStyle.LINEAR -> {
                decoration.value = null
                manager.value = LayoutManagers.linear()
                trigger.trig()
                origin.refresh()
            }

            ShelfLayoutStyle.GRID -> {
                val margin = 16.dp.toPx()
                decoration.value = object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        when (position % BookshelfFrag.GRID_SPAN_COUNT) {
                            1 -> {
                                // 取余为1, 除第一排之外的每一排的第一个
                                outRect.left = margin
                            }

                            0 -> {
                                // 取余为0, 每一排的最后一个(第3个)
                                if (position != 0) outRect.right = margin
                            }
                        }
                    }
                }

                manager.value = LayoutManagers.grid(BookshelfFrag.GRID_SPAN_COUNT)
                trigger.trig()
                origin.refresh()
            }
        }
    }
}
