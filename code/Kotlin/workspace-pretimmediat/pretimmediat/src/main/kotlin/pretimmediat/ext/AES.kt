package pretimmediat.ext

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

private const val KEY_ALGORITHM = "AES"
private const val UNICODE_FORMAT = "UTF-8"
private const val CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"

private const val KEY = "12af922c46fda88212bb9a888465d1f6"

@Throws(Exception::class)
fun String.aes(): String {
    // 还原密钥
    val k: Key = toKey(KEY.toByteArray(charset(UNICODE_FORMAT)))

    // Security.addProvider(new BouncyCastleProvider());
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    // 初始化，设置为加密模式
    cipher.init(Cipher.ENCRYPT_MODE, k)
    // 执行操作
    return bytes2String(cipher.doFinal(this.toByteArray(charset(UNICODE_FORMAT))))
}

@Throws(Exception::class)
private fun toKey(key: ByteArray): Key {
    val secretKey: SecretKey = SecretKeySpec(key, KEY_ALGORITHM)
    return secretKey
}

private fun bytes2String(buf: ByteArray): String {
    val sb = StringBuffer()
    for (i in buf.indices) {
        var hex = Integer.toHexString(buf[i].toInt() and 0xFF)
        if (hex.length == 1) {
            hex = "0$hex"
        }
        sb.append(hex)
    }
    return sb.toString()
}