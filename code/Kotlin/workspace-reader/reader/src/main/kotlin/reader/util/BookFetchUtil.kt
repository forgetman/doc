package reader.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import reader.BookUpdateChapterNum
import reader.Bus
import reader.EventId
import reader.db.Db
import reader.model.BookCache
import reader.model.ChapterContent
import reader.model.Chapters
import reader.model.pack.unpack
import reader.network.api.CommonApi
import reader.network.createApi

/**
 * @author yuansui
 * @since 2018/12/28
 */
object BookFetchUtil {

    fun chapters(id: String?) =
        createApi<CommonApi>()
            .chapters(id)
            .unpack()
            .onEach {
                val size = it.size
                val lastName = it.last().name

                Db.async {
                    it.forEach { c ->
                        c.bookId = id
                    }

                    insert(Chapters(id, it))
                    updateBookChapter(id, size, lastName)
                }

                Bus.getInstance().send(BookUpdateChapterNum(EventId.UPDATE_BOOK_CHAPTER_NUM).apply {
                    bookId = id
                    number = size
                    this.lastName = lastName
                })
            }

    fun chaptersFromDb(id: String?) =
        Db.asFlow { getChapters(id)?.list ?: mutableListOf() }.flowOn(Dispatchers.IO)

    fun contentFromDb(id: String?, chapterId: String) =
        Db.asFlow {
            getBookCache(id, chapterId)
        }.filterNotNull().map {
            ChapterContent(chapterId, it.content)
        }.flowOn(Dispatchers.IO)

    fun content(id: String?, chapterId: String) =
        createApi<CommonApi>()
            .content(id, chapterId)
            .unpack()
            .filterNotNull()
            .onEach {
                Db.sync {
                    insert(BookCache().apply {
                        this.bookId = id
                        this.chapterId = chapterId
                        this.content = it
                    })
                }
            }
            .map {
                ChapterContent(chapterId, it)
            }.flowOn(Dispatchers.IO)
}