package vector.util

/**
 * 测量size的工具
 *
 * @author yuansui
 */
object SizeofUtil {
    private val longSizeTable: LongArray by lazy {
        longArrayOf(
            9L,
            99L,
            999L,
            9999L,
            99999L,
            999999L,
            9999999L,
            99999999L,
            999999999L,
            9999999999L,
            99999999999L,
            999999999999L,
            9999999999999L,
            99999999999999L,
            999999999999999L,
            9999999999999999L,
            99999999999999999L,
            999999999999999999L,
            java.lang.Long.MAX_VALUE
        )
    }

    /**
     * 判断long有多少位
     */
    fun ofLong(x: Long): Int {
        if (x < 0) {
            return ofLong(-x)
        }

        var i = 0
        while (true) {
            if (x <= longSizeTable[i]) {
                return i + 1
            }
            i++
        }
    }

    fun ofInt(x: Int): Int {
        if (x < 0) {
            return ofInt(-x)
        }

        var i = 0
        while (true) {
            if (x <= longSizeTable[i]) {
                return i + 1
            }
            i++
        }
    }
}

fun Long.sizeof(): Int = SizeofUtil.ofLong(this)
fun Int.sizeof(): Int = SizeofUtil.ofInt(this)
