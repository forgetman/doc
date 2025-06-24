package reader.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import reader.model.Book
import reader.model.BookCache
import reader.model.Chapters

@Dao
interface BookDAO {
    @Query("SELECT * FROM table_book ORDER BY readTime DESC")
    fun getBooks(): Flow<MutableList<Book>>

    @Query("SELECT * FROM table_book ORDER BY readTime DESC")
    fun getBooksList(): MutableList<Book>

    @Query("SELECT * FROM table_chapters WHERE id = :id")
    fun getChapters(id: String?): Chapters?

    @Query("SELECT * FROM table_chapters WHERE id = :id")
    fun hasChapters(id: String?): Boolean

    @Query("SELECT * FROM table_book WHERE id = :id")
    fun getBook(id: String?): Book?

    @Query("SELECT * FROM table_book WHERE id = :id")
    fun hasBook(id: String?): Boolean

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: Book)

    @Query("UPDATE table_book SET `index` = :index WHERE id = :id")
    fun updateBookIndex(id: String?, index: Int)

    /**
     * 更新书本 关于 章节的部分信息
     * @param number 章节数量
     * @param lastName 最新章节描述
     * @param id 书本id
     */
    @Query("UPDATE table_book SET chapterNum = :number, newChapterDesc = :lastName WHERE id = :id")
    fun updateBookChapter(id: String?, number: Int, lastName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Book)

    @Update
    fun updateBooks(books: List<Book>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Chapters)

    @Delete
    fun delete(item: Book)

    @Query("SELECT * FROM table_book_caches WHERE chapterId = :chapterId AND bookId = :bookId")
    fun getBookCache(bookId: String?, chapterId: String?): BookCache?

    @Query("SELECT * FROM table_book_caches WHERE chapterId = :chapterId AND bookId = :bookId")
    fun hasBookCache(bookId: String?, chapterId: String?): Boolean

    @Query("DELETE FROM table_book_caches WHERE bookId = :bookId")
    fun deleteBookCache(bookId: String?)

    @Query("DELETE FROM table_chapters WHERE id = :bookId")
    fun deleteChapters(bookId: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: BookCache)
}