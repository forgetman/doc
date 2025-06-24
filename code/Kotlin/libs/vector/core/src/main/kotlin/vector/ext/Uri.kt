@file:Suppress("unused")

package vector.ext

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.provider.OpenableColumns
import logger.L
import sugar.ext.safeClose
import sugar.ext.safeUse
import vector.UriMode
import vector.appContext
import vector.util.Dir
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Throws(Exception::class)
fun Uri.inputStream(context: Context = appContext): InputStream? {
    return when (scheme) {
        null -> {
            // if scheme if null, maybe consider it as a file that missing "file://" prefix
            val p = path ?: return null
            FileInputStream(p)
        }

        else -> {
            context.contentResolver.openInputStream(this)
        }
    }
}

fun Uri.outputStream(contentResolver: ContentResolver): OutputStream? {
    return contentResolver.openOutputStream(this)
}

fun Uri.outputStream(context: Context = appContext, mode: UriMode = UriMode.WRITE): OutputStream? {
    return context.contentResolver.openOutputStream(this, mode.value)
}

fun Uri.copyTo(destFile: File, context: Context = appContext): Boolean {
    return copyTo(destFile.absolutePath, context)
}

fun Uri.copyTo(destPath: String, context: Context = appContext): Boolean {
    try {
        val inputStream = inputStream(context) ?: return false
        val outputStream: OutputStream = FileOutputStream(destPath)
        inputStream.copyTo(outputStream)
        inputStream.safeClose()
        outputStream.safeClose()
    } catch (e: Exception) {
        L.e(e)
        return false
    }
    return true
}

/**
 * 保留权限，除非对应uri发生改变，否则永不丢失
 * 可再次调用进行检查
 * 失败会崩溃
 */
fun Uri?.keepPermission() {
    if (this != null) {
        appContext.contentResolver.takePersistableUriPermission(
            this,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

/**
 * 为以后其他方式生成的uri获取权限，直到重启为止
 * 如果已经有打开的操作就不需要再调此方法
 */
fun Uri?.obtainPermission(context: Context = appContext, mode: UriMode = UriMode.READ) {
    this ?: return

    val descriptor = openDescriptor(context, mode)
    descriptor.safeClose()
}

// FIXME: 检测不出相册Uri
fun Uri?.exist(context: Context = appContext): Boolean {
    this ?: return false

    val descriptor = openDescriptor(context)
    return if (descriptor == null) false else {
        descriptor.safeClose()
        true
    }
}

fun Uri?.openDescriptor(
    context: Context = appContext,
    mode: UriMode = UriMode.READ
): AssetFileDescriptor? {
    return if (this != null) {
        try {
            context.contentResolver.openAssetFileDescriptor(this, mode.value)
        } catch (e: FileNotFoundException) {
            null
        }
    } else {
        null
    }
}

fun Uri.safeDescriptor(
    context: Context = appContext,
    mode: UriMode = UriMode.READ,
    block: (AssetFileDescriptor) -> Unit
) {
    openDescriptor(context, mode)?.let {
        block.invoke(it)
        it.safeClose()
    }
}

fun String.isUri(): Boolean {
    return startsWith(ContentResolver.SCHEME_CONTENT) || startsWith(ContentResolver.SCHEME_FILE)
}

@SuppressLint("Recycle")
fun Uri.getFileName(context: Context = appContext): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            val resolver = context.contentResolver
            val displayName = run {
                resolver.query(this, null, null, null, null)
                    ?.safeUse {
                        if (it.moveToFirst()) {
                            it.getStringOrNull(OpenableColumns.DISPLAY_NAME)
                        } else null
                    }
            }
            displayName
        }

        ContentResolver.SCHEME_FILE -> {
            val p = path ?: return null
            File(p).name
        }

        else -> null
    }
}

@SuppressLint("Recycle")
fun Uri.toFile(expectName: String? = null, context: Context = appContext): File? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            val resolver = context.contentResolver
            val displayName = run {
                resolver.query(this, null, null, null, null)
                    ?.safeUse {
                        if (it.moveToFirst()) {
                            it.getStringOrNull(OpenableColumns.DISPLAY_NAME)
                        } else null
                    }
            } ?: expectName ?: System.currentTimeMillis().toString()

            val file = File(Dir.Internal.cache, displayName)
            copyTo(file)
            file
        }

        ContentResolver.SCHEME_FILE -> {
            val p = path ?: return null
            File(p)
        }

        else -> null
    }
}

