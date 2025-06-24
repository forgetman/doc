@file:Suppress("DEPRECATION")

package vector.compat.media.api.download

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import vector.MimeType
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.OnConflictStrategy
import vector.ext.deleteAll
import vector.ext.ensureDirExist
import vector.ext.safeWriteBytes
import vector.ext.safeWriteText
import vector.ext.toBytes
import java.io.File
import java.io.IOException
import java.util.ArrayDeque

/**
 * @author yuansui
 * @since 2021/3/4
 */
internal class ApiImpl : Api {

    override fun save(
        context: Context,
        displayName: String?,
        secondaryPath: String?,
        content: Any,
        mimeType: MimeType,
        onConflict: OnConflictStrategy
    ): Boolean {
        val path = buildPath(secondaryPath)
        val name = displayName ?: System.currentTimeMillis().toString()

        var file = File(path, name.plus(mimeType.suffix))

        if (file.exists()) {
            when (onConflict) {
                OnConflictStrategy.DEFAULT -> {
                    var i = 1
                    while (true) {
                        val f = File(path, name.plus("($i)").plus(mimeType.suffix))
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

        if (content is String) {
            file.safeWriteText(content)
        } else {
            val bytes = content.toBytes() ?: return false
            file.safeWriteBytes(bytes)
        }
        return true
    }

    override fun delete(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): Boolean {
        if (displayName.isEmpty()) return false // 没有设置名称无法匹配
        return try {
            File(buildPath(secondaryPath), displayName.plus(mimeType.suffix)).delete()
        } catch (e: IOException) {
            false
        }
    }

    override fun delete(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): Int {
        return File(buildPath(secondaryPath)).deleteAll(includeSubFolder) {
            rule.match(it.name)
        }
    }

    override fun getData(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean
    ): List<MediaData> {
        val file = File(buildPath(secondaryPath))
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
                if (rule.match(f.name)) {
                    val path = f.absolutePath
                    data.add(MediaData(path, path.toUri()))
                }
            } else if (includeSubFolder) {
                f.listFiles()?.forEach {
                    deque.addLast(it)
                }
            }
            deque.pollFirst()
        }

        return data
    }

    override fun getData(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): MediaData? {
        val file = File(buildPath(secondaryPath), displayName.plus(mimeType.suffix))
        return if (file.exists()) {
            MediaData(file.path, file.name, file.absolutePath.toUri())
        } else null
    }

    private fun buildPath(secondaryPath: String?) = buildString {
        append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        append(File.separator)
        if (secondaryPath != null) {
            append(secondaryPath)
            append(File.separator)
        }
    }
}