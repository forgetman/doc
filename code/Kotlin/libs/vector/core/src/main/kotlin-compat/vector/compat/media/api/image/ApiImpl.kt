@file:Suppress("DEPRECATION")

package vector.compat.media.api.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.net.toUri
import sugar.ext.safeUse
import vector.MimeType
import vector.compat.ext.getPrivateSecondaryPath
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.OnConflictStrategy
import vector.ext.deleteAll
import vector.ext.ensureDirExist
import java.io.File
import java.util.ArrayDeque

/**
 * @author yuansui
 * @since 2020/11/7
 */
internal class ApiImpl : Api {

    override fun saveToAlbum(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean {
        return toAnyAlbum(
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
        return toAnyAlbum(
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

    private fun toAnyAlbum(
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
        val name = displayName ?: System.currentTimeMillis().toString()
        val mimeType = when (format) {
            Bitmap.CompressFormat.PNG -> MimeType.Image.Png.suffix
            Bitmap.CompressFormat.JPEG -> MimeType.Image.Jpeg.suffix
            else -> throw IllegalArgumentException("不支持的格式 = $format")
        }

        var file = File(path, name.plus(mimeType))

        if (file.exists()) {
            when (onConflict) {
                OnConflictStrategy.DEFAULT -> {
                    var i = 1
                    while (true) {
                        val f = File(path, name.plus("($i)").plus(mimeType))
                        if (f.exists()) {
                            i += 1
                            continue
                        }
                        file = f
                        break
                    }
                }

                OnConflictStrategy.REPLACE -> {
                    file.delete()
                }

                OnConflictStrategy.IGNORE -> return false
            }
        } else {
            File(path).ensureDirExist(false)
        }
        file.createNewFile()

        val result = file.outputStream().buffered().safeUse {
            bitmap.compress(format, quality, it)
        } ?: false
        if (result) scanFiles(context, listOf(file))

        return result
    }

    override fun deleteInAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean {
        return deleteInAnyAlbum(
            Environment.DIRECTORY_DCIM,
            secondaryPath,
            displayName
        )
    }

    override fun deleteInPrivateAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean {
        return deleteInAnyAlbum(
            Environment.DIRECTORY_PICTURES,
            getPrivateSecondaryPath(secondaryPath),
            displayName
        )
    }

    private fun deleteInAnyAlbum(
        directory: String,
        secondaryPath: String?,
        displayName: String
    ): Boolean {
        if (displayName.isEmpty()) return false // 没有设置名称无法匹配
        val path = buildPath(directory, secondaryPath)
        val count = File(path).deleteAll {
            // 判断[displayName]是否有传suffix
            val index = displayName.lastIndexOf(".")
            if (index != -1) {
                // 有
                it.name == displayName
            } else {
                // 无
                val maybePNG = displayName.plus(MimeType.Image.Png.suffix)
                val maybeJPEG = displayName.plus(MimeType.Image.Jpeg.suffix)
                it.name == maybeJPEG || it.name == maybePNG
            }
        }

        return count > 0
    }

    override fun deleteInAlbum(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        return deleteInAnyAlbum(
            context,
            Environment.DIRECTORY_DCIM,
            secondaryPath,
            rule,
            includeSubFolder
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
            Environment.DIRECTORY_PICTURES,
            secondaryPath,
            includeSubFolder
        )
    }

    private fun getDataInAnyAlbum(
        directory: String,
        secondaryPath: String?,
        includeSubFolder: Boolean
    ): List<MediaData> {
        val file = File(buildPath(directory, secondaryPath))
        if (!file.exists()) {
            return emptyList()
        }

        val data = mutableListOf<MediaData>()

        val deque = ArrayDeque<File>()
        deque.addLast(file)

        var f: File
        while (deque.isNotEmpty()) {
            f = deque.first
            if (f.isFile) {
                val path = f.absolutePath
                data.add(MediaData(path, path.toUri()))
            } else if (includeSubFolder) {
                f.listFiles()?.forEach {
                    deque.addLast(it)
                }
            }
            deque.pollFirst()
        }

        return data
    }

    private fun deleteInAnyAlbum(
        context: Context,
        directory: String,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        val path = buildPath(directory, secondaryPath)
        val file = File(path)
        return deleteAll(context, file, includeSubFolder) {
            rule.match(it.name)
        }
    }

    /**
     * 没有使用[File.deleteAll]
     * 因为需要对相册进行扫描通知相关的文件删除
     */
    private fun deleteAll(
        context: Context,
        file: File,
        includeSubFolder: Boolean,
        predicate: ((File) -> Boolean)? = null
    ): Int {
        if (!file.exists()) {
            return 0
        }

        val deque = ArrayDeque<File>()
        deque.addLast(file)

        val modifyFiles = mutableListOf<File>()

        var circleCount = 0
        var f: File
        while (deque.isNotEmpty()) {
            f = deque.first
            if (f.isFile) {
                if (predicate == null) {
                    val result = f.delete()
                    if (result) {
                        modifyFiles.add(f)
                    }
                } else if (predicate(f) && f.delete()) {
                    modifyFiles.add(f)
                }
            } else {
                if (circleCount > 0 && !includeSubFolder) {
                    // 停止查找子文件夹
                    break
                }
                f.listFiles()?.forEach {
                    deque.addLast(it)
                }
            }
            deque.pollFirst()
            circleCount++
        }

        scanFiles(context, modifyFiles)

        return modifyFiles.size
    }

    private fun buildPath(directory: String, secondaryPath: String?) = buildString {
        append(Environment.getExternalStoragePublicDirectory(directory))
        append(File.separator)
        if (secondaryPath != null) {
            append(secondaryPath)
            append(File.separator)
        }
    }

    /**
     * 通知相册扫描新的数据
     */
    private fun scanFiles(context: Context, files: List<File>) {
        if (files.isEmpty()) return

        val paths = files.map {
            it.absolutePath
        }
        MediaScannerConnection.scanFile(
            context,
            paths.toTypedArray(),
            null
        ) { _, uri ->
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = uri
            context.sendBroadcast(mediaScanIntent)
        }
    }
}