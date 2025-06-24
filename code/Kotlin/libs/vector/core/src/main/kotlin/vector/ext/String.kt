@file:Suppress("unused")

package vector.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import logger.L
import sugar.ext.systemService
import vector.EMPTY
import vector.appContext
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


private const val BLANK_UTF8_DEF = "%20"
private const val BLANK_INDEX = 32
private const val CN_RANGE_MIN = 19968 // \u4e00
private const val CN_RANGE_MAX = 171941 // \u9fa5
private const val HEX_CODE = "0123456789ABCDEF"


@OptIn(ExperimentalContracts::class)
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return !isNullOrEmpty()
}

@OptIn(ExperimentalContracts::class)
fun String?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }
    return !isNullOrBlank()
}

/**
 * 半角化。即将所有的数字、字母及标点全部转为半角字符
 */
fun String.toDBC(): String {
    return bufferString {
        this@toDBC.forEach {
            val char = when (val charInt = it.code) {
                12288 -> 32.toChar()
                in 65281..65374 -> (charInt - 65248).toChar()
                else -> it
            }
            append(char)
        }
    }
}

/**
 * 全角化。即将所有的数字、字母及标点全部转为全角字符，使它们与汉字同占两个字节
 */
fun String.toSBC(): String {
    return bufferString {
        this@toSBC.forEach {
            val char = when (val charInt = it.code) {
                32 -> 12288.toChar()
                in 33..127 -> (charInt + 65248).toChar()
                else -> it
            }
            append(char)
        }
    }
}

/**
 * 去除特殊字符或将所有中文标号替换为英文标号
 */
@Throws(PatternSyntaxException::class)
fun String.toEnSymbol(): String {
    val s = replace("【".toRegex(), "[")
        .replace("】".toRegex(), "]")
        .replace("！".toRegex(), "!") // 替换中文标号
    val regEx = "[『』]" // 清除掉特殊字符
    val p = Pattern.compile(regEx)
    val m = p.matcher(s)
    return m.replaceAll("").trim()
}

fun CharSequence?.copyToClipboard() {
    val cm = appContext.systemService<ClipboardManager>()
    cm.setPrimaryClip(ClipData.newPlainText(null, this))
}

/**
 * 根据字体大小及限制的PX长度来截取字符串
 *
 * @param textSize 字体像素大小
 * @param pxLength 像素长度限制
 * @param symbol   超时长度限制时追加的标识
 * @return 截取后的字符串
 */
fun CharSequence.cut(textSize: Float, pxLength: Float, symbol: String): String {
    val halfCharLen = textSize / 2

    var curLen = 0
    var count = 0
    for (i in 0 until length) {
        val chr1 = this[i].code
        curLen += if (chr1 in CN_RANGE_MIN..CN_RANGE_MAX) {
            // CN
            textSize.toInt()
        } else {
            halfCharLen.toInt()
        }

        if (curLen > pxLength) {
            break
        }

        count++
    }

    return bufferString {
        append(this@cut.subSequence(0, count))

        if (curLen > pxLength && symbol.isNotEmpty()) {
            append(symbol)
        }
    }
}

/**
 * 过滤空串
 */
fun String?.filterNull(): String {
    return this ?: EMPTY
}

/**
 * 把url的中文转换url格式
 */
fun String.toUtf8(): String {
    return buildString {
        forEach {
            val charInt = it.code
            if (charInt in 0..255) {
                // 非中文
                if (charInt == BLANK_INDEX) {
                    /**
                     * 空格需要特殊处理
                     * PS: 不能使用[java.net.URLEncoder.encode]处理，会变成'+'，浏览器无法辨识
                     */
                    this.append(BLANK_UTF8_DEF)
                } else {
                    this.append(it)
                }
            } else {
                val bytes: ByteArray = try {
                    it.toString().toByteArray()
                } catch (ex: Exception) {
                    ByteArray(0)
                }

                bytes.forEach { byte ->
                    var i: Int = byte.toInt()
                    if (i < 0) i += 256
                    this.append("%" + Integer.toHexString(i).uppercase())
                }
            }
        }
    }
}

/**
 * 首字母大写
 */
fun String.capitalize(): String {
    val chars = toCharArray()
    chars[0] = chars[0].uppercaseChar()
    return String(chars)
}

/**
 * String转InputStream
 */
@Throws(Exception::class)
fun String.toInputStream(): InputStream {
    return toByteArray(charset("ISO-8859-1")).inputStream()
}

/**
 * 16进制转10进制
 */
fun String.decodeHex(): String? = hexToBytes()?.utf8()

/**
 * 10进制转16进制
 */
fun String.hex(): String = toByteArray().hex()

fun String.md5(): String {
    return bufferString {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest: ByteArray = instance.digest(toByteArray())
            for (b in digest) {
                //获取低八位有效值
                val i: Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0$hexString"
                }
                append(hexString)
            }
        } catch (e: Exception/*NoSuchAlgorithmException*/) {
            L.e(e)
            clear()
        }
    }
}

inline fun bufferString(bufferAction: StringBuffer.() -> Unit): String =
    StringBuffer().apply(bufferAction).toString()

fun StringBuffer.clear() {
    if (isEmpty()) return
    delete(0, length - 1)
}

fun String.file(): File = File(this)

fun String.replaceLineBreak(): String = this.replace("\n", EMPTY)
fun String.replaceBlank(): String = this.replace(" ", EMPTY)

/**
 * Unicode(\u003d\u003d) 转 UTF8
 */
fun String.decodeUnicode(): String {
    return bufferString {
        val hex = this@decodeUnicode.split("\\\\u")
        for (i in 1 until hex.size) {
            // 转换出每一个代码点
            val data = hex[i].toInt(16)
            append(data.toChar())
        }
    }
}

fun String.toUnicode(): String {
    return bufferString {
        this@toUnicode.forEach {
            append("\\u" + Integer.toHexString(it.code))
        }
    }
}

fun String.charAt(position: Int) = toCharArray()[position]

fun String.charAtOrNull(position: Int) = if (position >= length) null else charAt(position)

/**
 * 每一个16进制字符是4bit，一个字节是8bit，所以两个16进制字符转换成1个字节，
 * 对于第1个字符，转换成byte以后左移4位，然后和第2个字符的byte做或运算，
 * 这样就把两个字符转换为1个字节。
 *
 * 用官方方法实现
 */
@OptIn(ExperimentalStdlibApi::class)
fun String.hexToBytes(): ByteArray? {
    if (isEmpty() || length % 2 != 0) return null
    return hexToByteArray()
}

fun String.exifInterface(): ExifInterface {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (isUri()) {
            this.toUri().openDescriptor()?.fileDescriptor?.let {
                ExifInterface(it)
            } ?: ExifInterface(this)
        } else {
            ExifInterface(this)
        }
    } else {
        ExifInterface(this)
    }
}

fun String.equalsAny(vararg searchStrings: String): Boolean {
    return searchStrings.find { this == it } != null
}

fun String.containsAny(vararg searchStrings: String): Boolean {
    return searchStrings.find { this.contains(it) } != null
}

fun String.startsWithAny(vararg searchStrings: String): Boolean {
    return searchStrings.find { this.startsWith(it) } != null
}

fun String.endsWithAny(vararg searchStrings: String): Boolean {
    return searchStrings.find { this.endsWith(it) } != null
}

fun String.classForName(): Class<*>? {
    return try {
        Class.forName(this)
    } catch (e: Exception) {
        L.e(e)
        null
    }
}