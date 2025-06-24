package lib.base.util

import java.util.*

object CalendarUtil {

    private const val MONTH_LAST = 1
    private const val MONTH_NEXT = -1

    fun getCurMonthCalendar(year: Int, month: Int): Calendar = getCalendar(year, month)

    private fun getCalendar(year: Int, month: Int): Calendar {
        val cal = Calendar.getInstance()

        // 只能使用月份偏移来计算, 因为到每个月的最后一天是不一样的, 用set方法会出问题
        val useMonth = month - cal.get(Calendar.MONTH)
        cal.set(Calendar.YEAR, year)
        cal.add(Calendar.MONTH, useMonth)

        return cal
    }

    fun getOffsetDate(year: Int, month: Int, monthOffset: Int): Calendar {
        val cal = getCalendar(year, month)
        cal.add(Calendar.MONTH, monthOffset)
        return cal
    }

    /**
     * 获取上月的日历
     */
    fun getLastMonthCalendar(year: Int, month: Int): Calendar {
        val calendar = getCalendar(year, month)
        calendar.add(Calendar.MONTH, MONTH_NEXT)
        return calendar
    }

    /**
     * 获取下月的日历
     */
    fun getNextMonthCalendar(year: Int, month: Int): Calendar {
        val calendar = getCalendar(year, month)
        calendar.add(Calendar.MONTH, MONTH_LAST)
        return calendar
    }

    fun getFirstWeekDayOfMonth(cal: Calendar): Int {
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE))
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    fun getLastWeekDayOfMonth(cal: Calendar): Int {
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
        return cal.get(Calendar.DAY_OF_WEEK)
    }

}
