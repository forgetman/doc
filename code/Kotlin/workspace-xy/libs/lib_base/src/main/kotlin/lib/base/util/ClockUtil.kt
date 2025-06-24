package lib.base.util

import android.content.Context

object ClockUtil {

    const val PER_MINUTE_DEGREE = 6 // 360 / 60
    const val PER_HOUR_DEGREE = 30 // 360 / 12
    const val EXACT_HOUR_DEGREE_BY_MINUTE = 0.5f // 360 / 12
    const val LAST_MINUTE_DEGREE = 360f - PER_MINUTE_DEGREE
    const val LAST_HOUR_DEGREE = 360f - EXACT_HOUR_DEGREE_BY_MINUTE

    const val DEGREE_MIN = 0
    const val DEGREE_MAX = 360

    private const val HOUR_COUNT_12 = 12
    private const val STR_HOUR_24 = "24"

    /**
     * 根据时间获取时钟的角度
     *
     * @param minute
     * @return
     */
    fun getMinuteDegree(minute: Int): Int {
        return minute * PER_MINUTE_DEGREE
    }

    fun getHourDegree(context: Context, hour: Int, minute: Int): Float {

        val c = context.contentResolver
        val strTimeFormat = android.provider.Settings.System.getString(
            c,
            android.provider.Settings.System.TIME_12_24
        )

        var ret = 0f
        if (strTimeFormat != null && strTimeFormat == STR_HOUR_24) {
            // strTimeFormat某些rom 12小时制时会返回null
            if (hour > HOUR_COUNT_12) {
                ret = (hour - HOUR_COUNT_12) * PER_HOUR_DEGREE + minute * EXACT_HOUR_DEGREE_BY_MINUTE
            } else {
                ret = hour * PER_HOUR_DEGREE + minute * EXACT_HOUR_DEGREE_BY_MINUTE
            }
        } else {
            ret = hour * PER_HOUR_DEGREE + minute * EXACT_HOUR_DEGREE_BY_MINUTE
        }

        return ret
    }

}
