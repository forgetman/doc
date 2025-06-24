package vector.ext

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import sugar.ext.safeUse


/**
 * 安全的query, 自动close
 */
@SuppressLint("Recycle")
fun <R> ContentResolver.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?,
    block: (Cursor) -> R?
): R? {
    return try {
        query(uri, projection, selection, selectionArgs, sortOrder)?.safeUse(block)
    } catch (e: Exception) {
        // CursorWindowAllocationException
        null
    }
}

/**
 * 安全的query, 自动close
 */
@SuppressLint("Recycle")
@RequiresApi(Build.VERSION_CODES.O)
fun <R> ContentResolver.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    queryArgs: Bundle?,
    cancellationSignal: CancellationSignal?,
    block: (Cursor) -> R?
): R? {
    return try {
        query(uri, projection, queryArgs, cancellationSignal)?.safeUse(block)
    } catch (e: Exception) {
        // CursorWindowAllocationException
        null
    }
}

/**
 * 安全的query, 自动close
 */
@SuppressLint("Recycle")
fun <R> ContentResolver.safeQuery(
    @RequiresPermission.Read uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?,
    cancellationSignal: CancellationSignal?,
    block: (Cursor) -> R?
): R? {
    return try {
        query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)?.safeUse(block)
    } catch (e: Exception) {
        // CursorWindowAllocationException
        null
    }
}