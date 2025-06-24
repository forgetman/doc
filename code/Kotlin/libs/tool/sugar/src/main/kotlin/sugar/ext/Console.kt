package sugar.ext

import java.io.DataOutputStream

/**
 * @author yuansui
 * @since 2022/7/27
 */
object Console {

    private const val PERMISSION_SU = "su"
    private const val PERMISSION_SH = "sh"
    private const val SYMBOL_ENTER = "\n"

    /**
     * 普通用户
     */
    fun writeAsSh(vararg content: String) {
        write(PERMISSION_SH, *content)
    }

    /**
     * root权限用户
     */
    fun writeAsSu(vararg content: String) {
        write(PERMISSION_SU, *content)
    }

    private fun write(command: String, vararg content: String) {
        Runtime.getRuntime().exec(command).apply {
            DataOutputStream(outputStream).safeUse { stream ->
                val size = content.size
                if (size == 1) {
                    stream.writeBytes(content[0])
                } else {
                    content.forEach { s ->
                        stream.writeBytes(s + SYMBOL_ENTER)
                    }
                }
            }
        }
    }
}