package vector.util

import logger.L
import okio.Buffer

/**
 * okio buffer偶现内部读写出错问题，尚未排查到根本原因，但可以尝试通过替换一个新的试试
 */
class Buffer(tag: String, val replaceAtCount: Int = 5) {
    private val logTag = "Buffer-$tag"

    private var buffer: Buffer = Buffer()

    val size: Long
        get() = buffer.size

    var listener: Listener? = null

    private var errorCount = 0

    interface Listener {
        fun onReplace()
    }


    fun write(data: ByteArray) {
        try {
            buffer.write(data)
        } catch (e: Exception) {
            L.e(logTag, "write error = ${e.message}")
            replaceIfNeed()
        }
    }

    fun clear() {
        try {
            buffer.clear()
        } catch (e: Exception) {
            L.e(logTag, "clear error = ${e.message}")
            replaceIfNeed()
        }
    }

    fun skip(byteCount: Long) {
        try {
            buffer.skip(byteCount)
        } catch (e: Exception) {
            L.e(logTag, "skip error = ${e.message}")
            replaceIfNeed()
        }
    }

    fun readByteArray(): ByteArray? {
        try {
            return buffer.readByteArray()
        } catch (e: Exception) {
            L.e(logTag, "readByteArray error = ${e.message}", e)
            replaceIfNeed()
        }
        return null
    }

    fun copyByteArray(): ByteArray? {
        try {
            return buffer.copy().readByteArray()
        } catch (e: Exception) {
            L.e(logTag, "copyByteArray error = ${e.message}")
            replaceIfNeed()
        }
        return null
    }

    private fun replaceIfNeed() {
        errorCount++
        L.i(logTag, "replaceIfNeed, count = $errorCount")
        if (errorCount >= replaceAtCount) {
            L.i(logTag, "replace")
            buffer = Buffer()
            listener?.onReplace()
            errorCount = 0
        }
    }

}