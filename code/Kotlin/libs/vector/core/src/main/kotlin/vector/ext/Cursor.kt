package vector.ext

import android.database.Cursor
import androidx.core.database.getBlobOrNull
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

fun Cursor.getInt(columnName: String): Int =
    getIntOrNull(columnName) ?: -1

fun Cursor.getIntOrNull(columnName: String): Int? =
    getIntOrNull(getColumnIndex(columnName))

fun Cursor.getIntOrElse(columnName: String, defaultValue: Int): Int =
    getIntOrNull(columnName) ?: defaultValue

fun Cursor.getFloat(columnName: String): Float =
    getFloatOrNull(columnName) ?: -1f

fun Cursor.getFloatOrNull(columnName: String): Float? =
    getFloatOrNull(getColumnIndex(columnName))

fun Cursor.getFloatOrElse(columnName: String, defaultValue: Float): Float =
    getFloatOrNull(columnName) ?: defaultValue

fun Cursor.getDouble(columnName: String): Double =
    getDoubleOrNull(columnName) ?: (-1).toDouble()

fun Cursor.getDoubleOrNull(columnName: String): Double? =
    getDoubleOrNull(getColumnIndex(columnName))

fun Cursor.getDoubleOrElse(columnName: String, defaultValue: Double): Double =
    getDoubleOrNull(columnName) ?: defaultValue

fun Cursor.getString(columnName: String): String =
    getStringOrNull(getColumnIndex(columnName)) ?: ""

fun Cursor.getStringOrNull(columnName: String): String? =
    getStringOrNull(getColumnIndex(columnName))

fun Cursor.getStringOrElse(columnName: String, defaultValue: String): String =
    getStringOrNull(columnName) ?: defaultValue

fun Cursor.getLong(columnName: String): Long =
    getLongOrNull(getColumnIndex(columnName)) ?: -1

fun Cursor.getLongOrNull(columnName: String): Long? =
    getLongOrNull(getColumnIndex(columnName))

fun Cursor.getLongOrElse(columnName: String, defaultValue: Long): Long =
    getLongOrNull(columnName) ?: defaultValue

fun Cursor.getBytesOrNull(columnName: String): ByteArray? =
    getBlobOrNull(getColumnIndex(columnName))