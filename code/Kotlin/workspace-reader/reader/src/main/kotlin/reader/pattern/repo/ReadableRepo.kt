package reader.pattern.repo

import dagger.hilt.android.scopes.FragmentScoped
import reader.db.Db
import reader.util.BookFetchUtil
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/11/8
 */
@FragmentScoped
class ReadableRepo @Inject constructor() {

    fun fetchText(bookId: String?, chapterId: String) =
        BookFetchUtil.content(bookId, chapterId)

    fun hasCache(bookId: String?, chapterId: String) = Db.sync { hasBookCache(bookId, chapterId) }
}