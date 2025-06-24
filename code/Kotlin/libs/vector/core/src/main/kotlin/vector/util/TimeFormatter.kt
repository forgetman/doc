package vector.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 转换时间显示样式
 *
 * @author yuansui
 */
object TimeFormatter {

    /**
     * 时间格式定义, 规则:
     */
    object FormatStyle {
        const val FULL_DATE = "yyyy-MM-dd" // 完整日期，年-月-日
        const val YEAR_AND_MONTH = "yyyy-MM" // 年和月
        const val MONTH_AND_DAY = "MM-dd" // 月和日
        const val ONLY_MONTH = "MM" // 仅显示月份
        const val ONLY_DAY = "dd" // 仅显示日期
        const val ONLY_SECOND = "ss" // 仅显示秒
        const val FULL_TIME_12H = "yyyy-MM-dd hh:mm:ss" // 12小时制的完整时间
        const val FULL_TIME_24H = "yyyy-MM-dd HH:mm:ss" // 24小时制的完整时间
        const val TIME_24H_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm" // 24小时制，不显示秒
        const val TIME_12H_ONLY_HOUR = "yyyy-MM-dd hh" // 12小时制，仅显示小时
        const val TIME_24H_ONLY_HOUR = "yyyy-MM-dd HH" // 24小时制，仅显示小时
        const val TIME_12H_FROM_HOUR = "hh:mm:ss" // 12小时制，从小时开始的时间
        const val TIME_24H_FROM_HOUR = "HH:mm:ss" // 24小时制，从小时开始的时间
        const val TIME_12H_HOUR_TO_MINUTE = "hh:mm" // 12小时制，小时到分钟的时间
        const val TIME_24H_HOUR_TO_MINUTE = "HH:mm" // 24小时制，小时到分钟的时间
        const val TIME_FROM_MINUTE = "mm:ss" // 从分钟开始的时间
        const val MONTH_DAY_TIME = "MM-dd HH:mm:ss" // 月日加时间
    }

    fun convert(time: String, format: String): String = convert(time.toLong(), format)

    fun convert(time: Long, format: String): String {
        return when (SizeofUtil.ofLong(time)) {
            10 -> second(time, format)
            13 -> milli(time, format)
            else -> time.toString()
        }
    }

    /**
     * 转换毫秒
     */
    private fun milli(milli: Long, format: String): String =
        SimpleDateFormat(format, Locale.getDefault()).format(milli)

    /**
     * 转换秒
     */
    private fun second(second: Long, format: String): String = milli(second * 1000, format)

    /**
     * 计算日期差
     */
    fun getTimeDiffer(t1: Long, t2: Long): Long {
        return (t1 - t2) / TimeUnit.DAYS.toMillis(1)
    }
}
