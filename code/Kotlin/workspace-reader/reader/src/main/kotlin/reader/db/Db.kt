package reader.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import coroutine.flow.asFlow
import coroutine.scope.CloseableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import reader.App
import reader.model.Book
import reader.model.BookCache
import reader.model.Chapters
import sugar.ext.runOnSubThread
import sugar.ext.throwIfNull

private typealias DaoSync<R> = BookDAO.() -> R
private typealias DaoAsync = suspend BookDAO.() -> Unit

/**
 * @author yuansui
 * @since 2018/5/22
 */
@Database(
    entities = [
        Book::class,
        Chapters::class,
        BookCache::class
    ], version = 2, exportSchema = false
)
abstract class Db : RoomDatabase() {

    companion object {
        private var db: Db? = null
            get() {
                if (field == null) {
                    field = makeSureDbExist()
                }
                return field
            }

        private val dao: BookDAO?
            get() = db?.getBookDAO()

        private var scope: CloseableCoroutineScope? = null
            get() {
                if (field == null) field = CloseableCoroutineScope()
                return field
            }

        private fun makeSureDbExist() = Room.databaseBuilder(App.context, Db::class.java, "db_book")
            .allowMainThreadQueries()
            .addMigrations(Migration1To2())
            .build()

        fun <R> asFlow(block: DaoSync<R>) = sync(block).asFlow()

        /**
         * 异步使用
         */
        fun async(block: DaoAsync) {
            val d = dao ?: return
            scope?.launch {
                launch(Dispatchers.Default) {
                    block(d)
                }
            }
        }

        /**
         * 同步使用
         */
        @Synchronized
        fun <R> sync(block: DaoSync<R>): R {
            return block(dao.throwIfNull())
        }

        fun close() {
            runOnSubThread {
                db?.close()
                db = null

                scope?.close()
                scope = null
            }
        }
    }

    abstract fun getBookDAO(): BookDAO
}