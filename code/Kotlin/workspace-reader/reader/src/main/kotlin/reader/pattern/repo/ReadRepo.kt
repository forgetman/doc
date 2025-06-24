package reader.pattern.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import reader.db.Db
import reader.model.Chapter
import reader.util.BookFetchUtil
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2018/9/8 0008
 */
@ViewModelScoped
class ReadRepo @Inject constructor() {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchChapters(id: String?): Flow<List<Chapter>> {
        val origin = if (Db.sync { hasChapters(id) }) {
            BookFetchUtil.chaptersFromDb(id)
        } else {
            BookFetchUtil.chapters(id)
        }

        return origin.flatMapConcat { old ->
            flow {
                val new = old.sortedBy {
                    it.id
                }
                emit(new)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun fetchText(id: String?, chapterId: String) =
        if (Db.sync { hasBookCache(id, chapterId) }) {
            BookFetchUtil.contentFromDb(id, chapterId)
        } else {
            BookFetchUtil.content(id, chapterId)
        }
}

