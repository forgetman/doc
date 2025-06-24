package lib.base.util

object StringUtil {

    fun leftPad(value: String, length: Int, input: String): String {
        return if (value.length >= length) {
            value
        } else {
            String().plus(input).plus(value)
        }
    }
}
