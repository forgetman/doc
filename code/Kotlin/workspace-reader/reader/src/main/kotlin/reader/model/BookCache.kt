package reader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import vector.EMPTY

/**
 * @author yuansui
 * @since 2018/9/20
 */
@Entity(tableName = "table_book_caches")
class BookCache {
    @PrimaryKey
    var chapterId: String = EMPTY

    var bookId: String? = null // 防止章节id相同查找错误
    var content: String? = null // 原始内容
}