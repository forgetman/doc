package reader.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @author yuansui
 * @since 2020/11/21
 */
class Migration1To2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE table_book ADD COLUMN readTime INTEGER NOT NULL DEFAULT 0")
    }
}