package reader.serv

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import logger.L
import reader.DownloadEvent
import reader.EventId
import reader.db.Db
import reader.model.Book
import reader.model.Chapter
import reader.sendMessage
import reader.util.BookFetchUtil
import vector.ext.toast
import vector.service.ServiceEx

enum class CacheType {
    ONLY_50,
    ALL_FROM_CURRENT,
    ALL
}

/**
 * @author yuansui
 * @since 2018/12/29
 */
@Creator
class CacheServ : ServiceEx() {

    @Extra
    lateinit var book: Book

    @Extra
    lateinit var type: CacheType

    private val set = hashSetOf<String>()

    override fun onHandleIntent(intent: Intent) {
        val id = book.id
        if (set.contains(id)) {
            toast("正在缓存中...")
            L.d("已有缓存任务 = $id")
            return
        }

        buildTask(id)
    }

    private fun buildTask(id: String) {
        val book = Db.sync { getBook(id) } ?: return

        val chapters = Db.sync { getChapters(book.id)?.list }
        if (chapters.isNullOrEmpty()) {
            L.d("缓存出错, 书籍查询不到章节")
            toast("缓存出错, 书籍查询不到任何章节")
            return
        }

        val task = DownloadTask()
        task.bookId = id
        task.chapters = chapters
        task.bookName = book.name
        when (type) {
            CacheType.ALL -> {
                task.fromIndex = 0
                task.size = chapters.size
            }

            CacheType.ALL_FROM_CURRENT -> {
                task.fromIndex = book.index
                task.size = chapters.size - book.index
            }

            CacheType.ONLY_50 -> {
                task.fromIndex = book.index
                task.size = 50
            }
        }

        // 立即执行
        task.begin()

        set.add(id)
    }

    inner class DownloadTask {
        var bookId: String? = null
        var bookName: String? = null
        var fromIndex = 0
        var size = 0
        var chapters: List<Chapter>? = null

        @OptIn(FlowPreview::class)
        fun begin() {
            var downloadedSize = 0

            fun onDownload() {
                downloadedSize++
                if (downloadedSize < size) {
                    sendMessage(DownloadEvent(EventId.CACHE_DOWNLOAD_PROGRESS).apply {
                        bookId = this@DownloadTask.bookId
                        any = "已缓存: $downloadedSize/$size"
                    })
                } else {
                    sendMessage(DownloadEvent(EventId.CACHE_DOWNLOAD_FINISH).apply {
                        bookId = this@DownloadTask.bookId
                        any = "已缓存: $size/$size"
                    })

                    set.remove(bookId)
                }
            }

            for (i in fromIndex until (size + fromIndex)) {
                val chapterId = chapters?.get(i)?.id ?: continue
                lifecycleScope.launch(Dispatchers.IO) {
                    val task = async {
                        val has = Db.sync { hasBookCache(bookId, chapterId) }
                        if (has) {
                            onDownload()
                        } else {
                            BookFetchUtil.content(book.id, chapterId).catch { throwable ->
                                L.e(throwable)
                            }.collect {
                                onDownload()
                            }
                        }
                    }
                    task.await()
                }
            }
        }
    }
}