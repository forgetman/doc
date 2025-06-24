package vector.compat.media.api.image

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import sugar.ext.safeUse
import vector.MimeType
import vector.compat.ext.getPrivateSecondaryPath
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.MediaSql
import vector.compat.media.OnConflictStrategy
import vector.ext.getLong
import vector.ext.getString
import java.io.File

/**
 * @author yuansui
 * @since 2020/11/7
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Api29Impl : Api {

    private val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    override fun saveToAlbum(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean {
        return saveToAnyAlbum(
            context,
            Environment.DIRECTORY_DCIM,
            bitmap,
            format,
            quality,
            secondaryPath,
            displayName,
            onConflict
        )
    }

    override fun saveToPrivateAlbum(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean {
        return saveToAnyAlbum(
            context,
            Environment.DIRECTORY_PICTURES,
            bitmap,
            format,
            quality,
            getPrivateSecondaryPath(secondaryPath),
            displayName,
            onConflict
        )
    }

    @SuppressLint("Recycle")
    private fun saveToAnyAlbum(
        context: Context,
        directory: String,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean {
        val path = buildPath(directory, secondaryPath)
        val values = ContentValues().apply {
            if (displayName != null) {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            }

            put(MediaStore.Images.Media.RELATIVE_PATH, path)
            put(MediaStore.Images.Media.IS_PENDING, 1)

            val mimeType = when (format) {
                Bitmap.CompressFormat.PNG -> MimeType.Image.Png.media
                Bitmap.CompressFormat.JPEG -> MimeType.Image.Jpeg.media
                else -> throw IllegalArgumentException("不支持的格式 = $format")
            }
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }

        val resolver = context.contentResolver

        when (onConflict) {
            OnConflictStrategy.DEFAULT -> {
                // do nothing
            }

            OnConflictStrategy.REPLACE -> {
                if (displayName != null) {
                    exist(context, path, displayName) {
                        resolver.delete(
                            contentUri,
                            MediaSql.build { where(MediaStore.Images.Media.DISPLAY_NAME) },
                            arrayOf(it)
                        )
                    }
                }
            }

            OnConflictStrategy.IGNORE -> {
                if (displayName != null) {
                    var find = false
                    exist(context, path, displayName) {
                        find = true
                    }
                    if (find) return false
                }
            }
        }

        val uri = resolver.insert(contentUri, values) ?: return false
        val result: Boolean = resolver.openOutputStream(uri)
            ?.buffered()
            ?.safeUse {
                bitmap.compress(format, quality, it)
            } ?: false

        // clear PENDING flag
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(
            uri,
            values,
            null,
            null
        )

        return result
    }

    override fun deleteInAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean {
        return deleteInAnyAlbum(context, Environment.DIRECTORY_DCIM, secondaryPath, displayName)
    }

    override fun deleteInAlbum(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        return deleteInAnyAlbum(context, Environment.DIRECTORY_DCIM, secondaryPath, rule, includeSubFolder)
    }

    override fun deleteInPrivateAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean {
        return deleteInAnyAlbum(
            context,
            Environment.DIRECTORY_PICTURES,
            getPrivateSecondaryPath(secondaryPath),
            displayName
        )
    }

    override fun deleteInPrivateAlbum(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        return deleteInAnyAlbum(
            context,
            Environment.DIRECTORY_PICTURES,
            getPrivateSecondaryPath(secondaryPath),
            rule,
            includeSubFolder
        )
    }

    override fun getDataInAlbum(
        context: Context,
        secondaryPath: String?,
        includeSubFolder: Boolean
    ): List<MediaData> {
        return getDataInAnyAlbum(
            context,
            Environment.DIRECTORY_DCIM,
            secondaryPath,
            includeSubFolder
        )
    }

    override fun getDataInPrivateAlbum(
        context: Context,
        secondaryPath: String?,
        includeSubFolder: Boolean
    ): List<MediaData> {
        return getDataInAnyAlbum(
            context,
            Environment.DIRECTORY_PICTURES,
            secondaryPath,
            includeSubFolder
        )
    }

    @SuppressLint("Recycle")
    private fun getDataInAnyAlbum(
        context: Context,
        directory: String,
        secondaryPath: String?,
        includeSubFolder: Boolean
    ): List<MediaData> {
        val resolver = context.contentResolver

        /**
         * 数据库查询不支持指定文件夹的子文件夹的查询, 只能查询到当前文件夹路径
         * 如果要查询子路径, 只能不限定RELATIVE_PATH(会查出所有包含其他app的数据), 然后自行判断
         */
        val mimeType = MediaStore.Images.Media.MIME_TYPE
        val selection: String?
        val selectionArgs: Array<String>?
        if (includeSubFolder) {
            // 搜索全部
            selection = MediaSql.build {
                where(mimeType)
                or(mimeType)
            }
            selectionArgs = arrayOf(MimeType.Image.Jpeg.media, MimeType.Image.Png.media)
        } else {
            selection = MediaSql.build {
                where(MediaStore.Images.Media.RELATIVE_PATH)
                andOr(mimeType, mimeType)
            }
            selectionArgs = arrayOf(
                buildPath(directory, secondaryPath),
                MimeType.Image.Jpeg.media,
                MimeType.Image.Png.media
            )
        }

        val data = mutableListOf<MediaData>()
        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Downloads._ID
            ),
            selection,
            selectionArgs,
            MediaStore.Images.Media.DATE_MODIFIED + " DESC",
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()
                val columnPath = cursor.getRelativePath()
                val columnId = cursor.getId()
                val uri = ContentUris.withAppendedId(contentUri, columnId)
                data.add(MediaData(columnPath, columnName, uri))
            }
        }

        return data
    }

    private fun deleteInAnyAlbum(
        context: Context,
        directory: String,
        secondaryPath: String?,
        displayName: String
    ): Boolean {
        val resolver = context.contentResolver
        // 判断[displayName]是否有传suffix
        val index = displayName.lastIndexOf(".")
        val count = if (index != -1) {
            resolver.delete(
                contentUri,
                MediaSql.build {
                    where(MediaStore.Images.Media.RELATIVE_PATH)
                    and(MediaStore.Images.Media.DISPLAY_NAME)
                },
                arrayOf(buildPath(directory, secondaryPath), displayName)
            )
        } else {
            val maybeJPEG = displayName.plus(MimeType.Image.Jpeg.suffix)
            val maybePNG = displayName.plus(MimeType.Image.Png.suffix)
            resolver.delete(
                contentUri,
                MediaSql.build {
                    where(MediaStore.Images.Media.RELATIVE_PATH)
                    andOr(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DISPLAY_NAME
                    )
                },
                arrayOf(buildPath(directory, secondaryPath), maybeJPEG, maybePNG)
            )
        }

        return count != 0
    }

    @SuppressLint("Recycle")
    private fun deleteInAnyAlbum(
        context: Context,
        directory: String,
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
            selection = MediaSql.build { where(MediaStore.Images.Media.RELATIVE_PATH) }
            selectionArgs = arrayOf(buildPath(directory, secondaryPath))
        }

        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DISPLAY_NAME
            ),
            selection,
            selectionArgs,
            null
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()

                // can delete
                if (rule.match(columnName)) {
                    if (includeSubFolder) {
                        val columnPath = cursor.getRelativePath()
                        val path = buildPath(directory, secondaryPath)
                        if (columnPath.startsWith(path)) {
                            count += resolver.delete(
                                contentUri,
                                MediaSql.build {
                                    where(MediaStore.Images.Media.DISPLAY_NAME)
                                    and(MediaStore.Images.Media.RELATIVE_PATH)
                                },
                                arrayOf(columnName, columnPath)
                            )
                        }
                    } else {
                        // 已限定文件夹
                        count += resolver.delete(
                            contentUri,
                            MediaSql.build { where(MediaStore.Images.Media.DISPLAY_NAME) },
                            arrayOf(columnName)
                        )
                    }
                }
            }
        }

        return count
    }

    @SuppressLint("Recycle")
    private fun exist(context: Context, path: String, displayName: String, action: (name: String) -> Unit) {
        val resolver = context.contentResolver
        resolver.query(
            contentUri,
            arrayOf(
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DISPLAY_NAME
            ),
            MediaSql.build { where(MediaStore.Images.Media.RELATIVE_PATH) },
            arrayOf(path),
            null
        )?.safeUse { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getDisplayName()
                val maybeJPEG = displayName.plus(MimeType.Image.Jpeg.suffix)
                val maybePNG = displayName.plus(MimeType.Image.Png.suffix)
                val name = when (columnName) {
                    maybeJPEG -> maybeJPEG
                    maybePNG -> maybePNG
                    else -> continue
                }
                if (columnName == name) {
                    action(name)
                    return
                }
            }
        }
    }

    private fun buildPath(directory: String, secondaryPath: String?) = buildString {
        append(directory)
        append(File.separator)
        if (secondaryPath != null) {
            append(secondaryPath)
            append(File.separator)
        }
    }

    private fun Cursor.getDisplayName(): String = getString(MediaStore.Images.Media.DISPLAY_NAME)
    private fun Cursor.getRelativePath(): String = getString(MediaStore.Images.Media.RELATIVE_PATH)
    private fun Cursor.getId(): Long = getLong(MediaStore.Images.Media._ID)
}