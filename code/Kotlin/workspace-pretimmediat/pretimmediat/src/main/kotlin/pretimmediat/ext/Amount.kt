package pretimmediat.ext

import java.text.NumberFormat
import java.util.Locale

private const val DOT = "."

/**
 * 转成金额格式
 * 先判断是否有小数点, 如果没有, 直接格式化, 如果有, 看小数点后的数值是否为0, 如果是0, 直接格式化, 如果不是0, 保留两位小数
 */
fun String?.formatMoney(): String {
    if (this.isNullOrEmpty()) return "0"
    return if (contains(DOT)) {
        NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
            .apply { maximumFractionDigits = 2 }
            .format(this.toFloat())
    } else {
        NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
            .apply { maximumFractionDigits = 0 }
            .format(this.toFloat())
    }
}

fun Long.formatMoney() = toString().formatMoney()
fun Double.formatMoney() = toString().formatMoney()
fun Float.formatMoney() = toString().formatMoney()
fun Int.formatMoney() = toString().formatMoney()

object ForDataBinding {
    @JvmStatic
    fun formatMoney(value: String): String {
        return value.formatMoney()
    }

    @JvmStatic
    fun formatMoney(value: Long): String {
        return value.formatMoney()
    }

    @JvmStatic
    fun formatMoney(value: Double): String {
        return value.formatMoney()
    }

    @JvmStatic
    fun formatMoney(value: Int): String {
        return value.formatMoney()
    }

    @JvmStatic
    fun formatMoney(value: Float): String {
        return value.formatMoney()
    }
}