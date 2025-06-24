package vector.compat.media.api.download

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import sugar.ext.safeUse
import vector.MimeType
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.MediaSql
import vector.compat.media.OnConflictStrategy
import vector.ext.getLong
import vector.ext.getString
import vector.ext.toBytes
import java.io.File

/**
 * @author yuansui
 * @since 2021/3/4
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Api29Impl : Api {

    /**
     * [MediaStore.Downloads.getContentUri]with[MediaStore.VOLUME_EXTERNAL_PRIMARY]: content://media/external_primary/downloads
     * [MediaStore.Downloads.getContentUri]with[MediaStore.VOLUME_EXTERNAL]: content://media/external/downloads
     * [MediaStore.Downloads.EXTERNAL_CONTENT_URI]: content://media/external/downloads
     * [MediaStore.Downloads.INTERNAL_CONTENT_URI]: content://media/internal/downloads
     */
    private val contentUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    @SuppressLint("Recycle")
    override fun save(
        context: Context,
        displayName: String?,
        secondaryPath: String?,
        content: Any,
        mimeType: MimeType,
        onConflict: OnConflictStrategy
    ): Boolean {
        val path = buildPath(secondaryPath)
        val values = ContentValues().apply {
            put(
                MediaStore.Downloads.DISPLAY_NAME,
                displayName ?: System.currentTimeMillis().toString()
            )
            put(MediaStore.Downloads.RELATIVE_PATH, path)
            put(MediaStore.Downloads.IS_PENDING, 1)
            put(MediaStore.Downloads.MIME_TYPE, mimeType.media)
        }

        val contentUri = contentUri
        val resolver = context.contentResolver

        when (onConflict) {
            OnConflictStrategy.DEFAULT -> {
                // do nothing
            }

            OnConflictStrategy.REPLACE -> {
                if (displayName != null) {
                    delete(context, displayName, secondaryPath, mimeType)
                }
            }

            OnConflictStrategy.IGNORE -> {
                if (displayName != null) {
                    var find = false
                    exist(context, path, displayName, mimeType) {
                        find = true
                    }
                    if (find) return false
                }
            }
        }

        val uri = resolver.insert(contentUri, values) ?: return false
        val result: Boolean = resolver.openOutputStream(uri)
            ?.safeUse {
                it.write(content.toBytes())
                true
            } == true

        // clear PENDING flag
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(
            uri,
            values,
            null,
            null
        )

        return result
    }

    override fun delete(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): Boolean {
        val result = context.contentResolver.delete(
            contentUri,
            MediaSql.build {
                where(MediaStore.Downloads.RELATIVE_PATH)
                and(MediaStore.Downloads.DISPLAY_NAME)
            },
            arrayOf(buildPath(secondaryPath), displayName.plus(mimeType.suffix))
        )
        return result != 0
    }

    @SuppressLint("Recycle")
    override fun delete(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        val resolver = context.contentResolver

        var count = 0

        /**
         * 数据库查询并不支持子文件夹的查询, 只能查询到当前文件夹路径
         * 如果要查询子路径, 只能不限定RELATIVE_PATH(会查出所有包含其他app的数据), 然后自行判断
         */
        val selection: String?
        val selectionArgs: Array<String>?
        if (includeSubFolder) {
            // 搜索全部
            selection = null
            selectionArgs = null
        } else {
            selection = MediaSql.build { where(MediaStore.Downloads.RELATIVE_PATH) }
            selectionArgs = arrayOf(buildPath(secondaryPath))
        }

        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Downloads.RELATIVE_PATH,
                MediaStore.Downloads.DISPLAY_NAME
            ),
            selection,
            selectionArgs,
            null
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()

                if (rule.match(columnName)) {
                    // 可以删除
                    if (includeSubFolder) {
                        val columnPath = cursor.getRelativePath()
                        val path = buildPath(secondaryPath)
                        if (columnPath.startsWith(path)) {
                            count += resolver.delete(
                                contentUri,
                                MediaSql.build {
                                    where(MediaStore.Downloads.DISPLAY_NAME)
                                    and(MediaStore.Downloads.RELATIVE_PATH)
                                },
                                arrayOf(columnName, columnPath)
                            )
                        }
                    } else {
                        // 已限定文件夹
                        count += resolver.delete(
                            contentUri,
                            MediaSql.build { where(MediaStore.Downloads.DISPLAY_NAME) },
                            arrayOf(columnName)
                        )
                    }
                }
            }
        }

        return count
    }

    @SuppressLint("Recycle")
    override fun getData(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): List<MediaData> {
        val resolver = context.contentResolver

        /**
         * 数据库查询并不支持子文件夹的查询, 只能查询到当前文件夹路径
         * 如果要查询子路径, 只能不限定RELATIVE_PATH(会查出所有包含其他app的数据), 然后自行判断
         */
        val selection: String?
        val selectionArgs: Array<String>?
        if (includeSubFolder) {
            // 搜索全部
            selection = null
            selectionArgs = null
        } else {
            selection = MediaSql.build { where(MediaStore.Downloads.RELATIVE_PATH) }
            selectionArgs = arrayOf(buildPath(secondaryPath))
        }

        val data = mutableListOf<MediaData>()
        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Downloads.RELATIVE_PATH,
                MediaStore.Downloads.DISPLAY_NAME,
                MediaStore.Downloads._ID
            ),
            selection,
            selectionArgs,
            null
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()

                if (rule.match(columnName)) {
                    while (cursor.moveToNext()) {
                        val columnPath = cursor.getRelativePath()
                        val columnId = cursor.getId()
                        val uri = ContentUris.withAppendedId(contentUri, columnId)
                        data.add(MediaData(columnPath, columnName, uri))
                    }
                }
            }
        }

        return data
    }

    @SuppressLint("Recycle")
    override fun getData(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): MediaData? {
        val resolver = context.contentResolver
        val name = displayName.plus(mimeType.suffix)
        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Downloads.RELATIVE_PATH,
                MediaStore.Downloads.DISPLAY_NAME,
                MediaStore.Downloads._ID
            ),
            MediaSql.build {
                where(MediaStore.Downloads.RELATIVE_PATH)
                and(MediaStore.Downloads.DISPLAY_NAME)
            },
            arrayOf(buildPath(secondaryPath), name),
            null
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()
                val columnPath = cursor.getRelativePath()
                val columnId = cursor.getId()
                val uri = ContentUris.withAppendedId(contentUri, columnId)
                return MediaData(columnPath, columnName, uri)
            }
        }
        return null
    }

    @SuppressLint("Recycle")
    private fun exist(
        context: Context,
        path: String,
        displayName: String,
        mimeType: MimeType,
        action: (name: String) -> Unit
    ) {
        val resolver = context.contentResolver
        val name = displayName.plus(mimeType.suffix)
        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Downloads.RELATIVE_PATH,
                MediaStore.Downloads.DISPLAY_NAME
            ),
            MediaSql.build {
                where(MediaStore.Downloads.RELATIVE_PATH)
                and(MediaStore.Downloads.DISPLAY_NAME)
            },
            arrayOf(path, name),
            null
        )?.safeUse {
            if (it.columnCount > 0) {
                action(name)
            }
        }
    }

    private fun buildPath(secondaryPath: String?) = buildString {
        append(Environment.DIRECTORY_DOWNLOADS)
        append(File.separator)
        if (secondaryPath != null) {
            append(secondaryPath)
            append(File.separator)
        }
    }

    private fun Cursor.getDisplayName(): String = getString(MediaStore.Downloads.DISPLAY_NAME)
    private fun Cursor.getRelativePath(): String = getString(MediaStore.Downloads.RELATIVE_PATH)
    private fun Cursor.getId(): Long = getLong(MediaStore.Downloads._ID)
}