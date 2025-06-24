package vector.ext

import sugar.ext.safeUse
import java.io.ByteArrayOutputStream
import java.io.File

infix fun Byte.and(other: Int): Int = this.toInt() and other
infix fun Byte.or(other: Int): Int = this.toInt() or other

/**
 * bytes转换成16进制的string
 */
fun ByteArray.hex(): String {
    return bufferString {
        this@hex.forEach {
            var hex = Integer.toHexString(it and 0xFF)
            if (hex.length == 1) hex = "0$hex"
            append(hex)
        }
    }
}

fun ByteArray.utf8() = String(this, Charsets.UTF_8)

fun ByteArray.file(filePath: String, fileName: String) =
    File(filePath, fileName).safeWriteBytes(this)

fun Any.toBytes(): ByteArray? {
    return when (this) {
        is ByteArray -> this
        is String -> this.toByteArray()
        is Int -> byteArrayOf(
            (this shr 24 and 0xFF).toByte(),
            (this shr 16 and 0xFF).toByte(),
            (this shr 8 and 0xFF).toByte(),
            (this and 0xFF).toByte()
        )

        else -> {
            ByteArrayOutputStream().safeUse {
                it.obj().writeObject(this)
                it.toByteArray()
            }
        }
    }
}

fun ByteArray.subBytes(begin: Int, length: Int): ByteArray {
    val originSize = this.size
    var useLen = length
    if (begin + length >= originSize) {
        useLen = originSize - begin
    }
    val bytes = ByteArray(useLen)
    System.arraycopy(this, begin, bytes, 0, useLen)
    return bytes
}