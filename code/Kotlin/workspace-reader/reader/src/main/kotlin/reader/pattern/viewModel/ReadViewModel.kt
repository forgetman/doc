package reader.pattern.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import reader.BookUpdateReadIndex
import reader.EventId
import reader.db.Db
import reader.model.Chapter
import reader.model.Readable
import reader.model.toReadables
import reader.pattern.frag.ReadableFragCreator
import reader.pattern.repo.ReadRepo
import reader.sendMessage
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.build
import vector.app.viewmodel.ViewModelEx
import vector.bindingadapter.CurrentItem
import vector.ext.toast
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019-09-18
 */
@HiltViewModel
class ReadViewModel @Inject constructor(
    private val repo: ReadRepo,
    app: Application
) : ViewModelEx(app) {

    companion object {
        private const val LOG_TAG = "ReadViewModel"
    }

    lateinit var bookId: String

    val chapters = MutableStateFlow<List<Chapter>>(emptyList())

    val currItem = MutableStateFlow<CurrentItem?>(null)
    val pager = MutableStateFlow<FragPager?>(null)
    val currChapterIndex = MutableStateFlow<Int?>(null)

    val data: StateFlow<List<Readable>> = chapters.map {
        it.map { chapter ->
            chapter.toReadables()
        }.flatten()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private var requireResetCurrItem = false // 章节下载后影响了index, 是否需要重设
    private var reloadByConfigChanged = false // 是否需要重新加载config和所有章节
    private var reloadKeepChapterId: String? = null // 保存reload时候的章节id
    private var firstLoad = true // 第一次加载数据

    override fun onCreate() {
        data.onEach {
            when {
                firstLoad -> {
                    pager.value = FragPager.build(
                        it.size,
                        requiredCurrentItem = requireNotNull(currItem.value).index
                    ) { position ->
                        ReadableFragCreator.create(it[position]).get()
                    }
                    firstLoad = false
                }

                requireResetCurrItem -> {
                    pager.value = FragPager.build(
                        it.size,
                        requiredCurrentItem = requireNotNull(currItem.value).index
                    ) { position ->
                        ReadableFragCreator.create(it[position]).get()
                    }
                }

                reloadByConfigChanged -> {
                    // 找到字体大小等变化后, 之前的chapter id在哪个index
                    var index = findChapterIndexById(reloadKeepChapterId)
                    if (index == -1) index = 0
                    currItem.value = CurrentItem(index, false)

                    pager.value = FragPager.build(it.size) { position ->
                        ReadableFragCreator.create(it[position]).get()
                    }
                    reloadByConfigChanged = false
                }

                else -> {
                    pager.value = FragPager.build(it.size) { position ->
                        ReadableFragCreator.create(it[position]).get()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun fetchChapters(id: String?) = repo.fetchChapters(id).onEach {
        chapters.value = it
    }.catch { e ->
        L.e(LOG_TAG, "fetchChapters", e)
    }

    fun onReadableIndexChanged(index: Int) {
        if (requireResetCurrItem) {
            requireResetCurrItem = false
            return
        }

        val readable = data.value.getOrNull(index) ?: return
        val chapterIndex = findChapterIndexById(readable.chapterId ?: return)
        if (chapterIndex == -1) return
        updateReadChapterIndex(chapterIndex)
    }

    fun onChapterDownload(chapterId: String) {
        findAndReplace(chapterId) { findIndex, readables ->
            val currIndex = currItem.value?.index ?: return@findAndReplace
            if (findIndex < currIndex) {
                requireResetCurrItem = true
                currItem.value = CurrentItem(currIndex + readables.lastIndex, false)
            }
        }
    }

    private fun updateReadChapterIndex(index: Int) {
        val lastChapterIndex = currChapterIndex.value
        if (lastChapterIndex == index) return

        Db.async { updateBookIndex(bookId, index) }
        sendMessage(BookUpdateReadIndex(EventId.UPDATE_BOOK_READ_INDEX).apply {
            this.bookId = this@ReadViewModel.bookId
            this.index = index
        })
        currChapterIndex.value = index
    }

    private fun findAndReplace(
        chapterId: String,
        action: ((findIndex: Int, readables: List<Readable>) -> Unit)? = null
    ) {
        val cs = chapters.value
        if (cs.isEmpty()) return

        val chapterIndex = findChapterIndexById(chapterId)
        if (chapterIndex == -1) return

        val chapter = cs[chapterIndex]
        if (chapter.prepared) return

        repo.fetchText(bookId, chapterId)
            .onEach {
                chapter.text = it.content
                chapter.preparePages()

                val all = data.value
                val findIndex: Int = all.indexOfFirst { r ->
                    r.chapterId == chapter.id
                }
                if (findIndex != -1) {
                    // 替换章节内容
                    // FIXME: 逻辑可能有点问题
                    action?.invoke(findIndex, chapter.toReadables())
                    chapters.value = cs.toMutableList().apply {
                        set(chapterIndex, chapter)
                    }
                }
            }.catch { exception ->
                L.e(exception)
            }.launchIn(viewModelScope)
    }

    private fun findChapterIndexById(chapterId: String?): Int {
        return chapters.value.indexOfFirst {
            it.id == chapterId
        }
    }

    fun switchChapterTo(chapterIndex: Int) {
        val readableIndex = data.value.indexOfFirst {
            it.chapterId == chapters.value.getOrNull(chapterIndex)?.id
        }
        if (readableIndex == -1) return

        currItem.value = CurrentItem(readableIndex, false)
        updateReadChapterIndex(chapterIndex)
    }

    fun switchToPreviousChapter() {
        val curr = currChapterIndex.value ?: return

        val previous = curr - 1
        if (previous < 0) {
            toast("已经是第一章")
            return
        }
        switchChapterTo(previous)
    }

    fun switchToNextChapter() {
        val curr = currChapterIndex.value ?: return

        val next = curr + 1
        if (next > chapters.value.lastIndex) {
            toast("已经是最后一章")
            return
        }
        switchChapterTo(next)
    }

    fun reloadDataFromConfig() {
//        PageDrawer.reloadConfig()

        // 保存reload之前的chapterId
        val item = data.value.getOrNull(currItem.value?.index ?: 0)
        reloadKeepChapterId = item?.chapterId

        chapters.value = emptyList()
        reloadByConfigChanged = true

        // TODO: 取消之前所有没完成的任务, 可以借助自定义scope来结束
        fetchChapters(bookId).launchIn(viewModelScope)
    }

    fun switchPageToPrevious() {
        val curr = currItem.value?.index ?: return
        val previous = curr - 1
        if (previous < 0) {
            toast("前面没有了")
            return
        }
        currItem.value = CurrentItem(previous, false)
    }

    fun switchPageToNext() {
        val curr = currItem.value?.index ?: return
        val next = curr + 1
        if (next > data.value.lastIndex) {
            toast("后面没有了")
            return
        }
        currItem.value = CurrentItem(next, false)
    }
}