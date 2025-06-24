@file:Suppress("unused")

package vector.ext

import android.content.Context
import android.net.Uri
import logger.L
import sugar.ext.safeClose
import vector.appContext
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.ArrayDeque

/**
 * 创建文件
 * @return 返回true, 存在或创建成功; 返回false, 创建路径或文件失败
 */
fun File.mkFile(): Boolean {
    if (exists()) {
        // 如果本身是以文件夹形式存在, 无法变为文件, 暂时不自动处理文件类型的改变(删除再创建)的流程
        return !isDirectory
    }
    val parentExit = mkParentDirs()
    if (!parentExit) return false
    return try {
        createNewFile()
        true
    } catch (e: IOException) {
        L.e(e)
        false
    }
}

/**
 * 创建父路径
 */
fun File.mkParentDirs(): Boolean {
    if (exists()) return true
    val parent = parentFile
    return parent != null && (parent.exists() || parent.mkdirs())
}

/**
 * 确保路径存在
 * @param isDirectory 是否为文件夹路径
 */
fun File.ensureDirExist(isDirectory: Boolean = true): Boolean {
    return if (isDirectory) {
        if (exists()) true else mkdirs()
    } else {
        mkParentDirs()
    }
}

fun String.mkFile(): Boolean = file().mkFile()
fun String.mkParentDirs(): Boolean = file().mkParentDirs()
fun String.ensureDirExist(isDirectory: Boolean = true) = file().ensureDirExist(isDirectory)
fun String.fileExists(): Boolean = file().exists()

fun File.safeWriteText(text: String, charset: Charset = Charsets.UTF_8) {
    try {
        writeText(text, charset)
    } catch (e: Throwable) {
        L.e(e)
    }
}

fun File.safeAppendText(text: String, charset: Charset = Charsets.UTF_8) {
    try {
        appendText(text, charset)
    } catch (e: Throwable) {
        L.e(e)
    }
}

fun File.safeWriteBytes(array: ByteArray) {
    try {
        writeBytes(array)
    } catch (e: Throwable) {
        L.e(e)
    }
}

fun File.safeAppendBytes(array: ByteArray) {
    try {
        appendBytes(array)
    } catch (e: Throwable) {
        L.e(e)
    }
}

fun File.safeReadText(charset: Charset = Charsets.UTF_8): String? {
    return try {
        readText(charset)
    } catch (e: Throwable) {
        L.e(e)
        null
    }
}

/**
 * 获取文件(夹)大小
 * @return 文件大小(字节)
 */
fun File.getSize(): Long {
    if (this.exists()) {
        if (this.isFile) {
            return this.length()
        } else {
            val deque = ArrayDeque<File>()
            deque.addLast(this)

            var size = 0L
            var file: File
            while (deque.isNotEmpty()) {
                file = deque.first
                if (file.isFile) {
                    size += file.length()
                } else {
                    file.listFiles()?.forEach {
                        deque.addLast(it)
                    }
                }
                deque.pollFirst()
            }

            return size
        }
    } else {
        return 0
    }
}

/**
 * 删除指定File，支持目录和文件
 * <p>
 *     利用栈的特性后进先出
 *     先把文件夹内文件删掉再删文件夹
 * </p>
 *
 * @return 成功删除的文件数量
 */
fun File?.deleteAll(includeSubFolder: Boolean = true, predicate: ((File) -> Boolean)? = null): Int {
    if (this == null || !exists()) return 0

    var count = 0

    val deque = ArrayDeque<File>()
    deque.addLast(this)

    var firstCircle = true
    while (deque.isNotEmpty()) {
        val file = deque.first
        if (file.isFile) {
            if (predicate == null) {
                if (file.delete()) count++
            } else if (predicate(file) && file.delete()) {
                count++
            }
        } else {
            // 文件夹
            if (!firstCircle && !includeSubFolder) {
                // 停止查找子文件夹
                deque.pollFirst()
                continue
            }
            file.listFiles()?.forEach {
                deque.addLast(it)
            }
        }
        deque.pollFirst()
        firstCircle = false
    }

    return count
}

fun File.copyTo(dest: File): Boolean {
    try {
        copyTo(dest, true)
    } catch (e: Exception) {
        // include NoSuchFileException, FileAlreadyExistsException, FileSystemException
        L.e(e)
        return false
    }
    return true
}

fun File.copyTo(destPath: String): Boolean {
    return copyTo(destPath.file())
}

fun File.copyTo(uri: Uri, context: Context = appContext): Boolean {
    try {
        val inputStream = inputStream()
        val outputStream = uri.outputStream(context) ?: return false
        inputStream.copyTo(outputStream)
        inputStream.safeClose()
        outputStream.safeClose()
        return true
    } catch (e: Exception) {
        L.e(e)
        return false
    }
}