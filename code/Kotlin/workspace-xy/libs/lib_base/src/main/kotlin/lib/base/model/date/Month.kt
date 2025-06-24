package lib.base.model.date

import lib.base.Constants
import lib.base.model.FirstDayType
import lib.base.util.CalendarUtil
import logger.L
import java.util.*


class Month(dateYear: Int, dateMonth: Int) {

    companion object {
        private const val FD_SUNDAY = 1
    }

    private var perLineDays: MutableList<Day>? = null
    lateinit var lines: MutableList<MutableList<Day>>

    private val currCalDates: IntArray
    private val nextCalDates: IntArray

    private var currUseDateIdx: Int = 0
    private var nextUseDateIdx: Int = 0

    // 当月的日历
    private val currCal: Calendar = CalendarUtil.getCurMonthCalendar(dateYear, dateMonth)

    // 上月的日历 
    private val nextCal: Calendar = CalendarUtil.getNextMonthCalendar(dateYear, dateMonth)

    // 下月的日历
    private val lastCal: Calendar = CalendarUtil.getLastMonthCalendar(dateYear, dateMonth)

    val year: Int
        get() = currCal.get(Calendar.YEAR)

    val month: Int
        get() = currCal.get(Calendar.MONTH)

    val dayCount: Int
        get() = lines[1].size

    val lineCount: Int
        get() = lines.size

    init {
        val currMaxDateCount = currCal.getActualMaximum(Calendar.DATE)
        currCalDates = IntArray(currMaxDateCount)
        for (i in 0 until currMaxDateCount) {
            currCalDates[i] = i + 1
        }
        currUseDateIdx = 0

        val nextMaxDateCount = nextCal.getActualMaximum(Calendar.DATE)
        nextCalDates = IntArray(nextMaxDateCount)
        for (i in 0 until nextMaxDateCount) {
            nextCalDates[i] = i + 1
        }
        nextUseDateIdx = 0
    }

    /**
     * 外部设置行数, 使4X4和8X4可以一直使用并列一周的设置
     *
     * @param rowCount
     */
    fun setDataByWeekRowCount(rowCount: Int, firstDayType: FirstDayType) {

        when (rowCount) {
            Constants.KWeekRowCountOne -> {
                lines = ArrayList(Constants.KWeekRowOneLineCount)
                when (firstDayType) {
                    FirstDayType.MONDAY -> addFirstDayMondayData()
                    FirstDayType.SUNDAY -> addFirstDaySundayData()
                }
            }
            Constants.KWeekRowCountTwo -> {
                lines = ArrayList(Constants.KWeekRowTwoLineCount)
                when (firstDayType) {
                    FirstDayType.MONDAY -> addWeekRowTwoFdMondayData()
                    FirstDayType.SUNDAY -> addWeekRowTwoFdSundayData()
                }
            }
            else -> {
                // 默认皮肤
                lines = ArrayList(Constants.KWeekRowOneLineCount)
                when (firstDayType) {
                    FirstDayType.MONDAY -> addFirstDayMondayData()
                    FirstDayType.SUNDAY -> addFirstDaySundayData()
                }
            }
        }
    }

    private fun addWeekRowTwoFdMondayData() {

        // 先计算1号是星期几
        // firstWeekDay 从1开始即周日
        val firstWeekDay = CalendarUtil.getFirstWeekDayOfMonth(currCal) + 1
        // TenLog.d(TAG, "firstWeekDay = " + firstWeekDay);

        for (i in 0 until Constants.KWeekRowTwoLineCount) {
            perLineDays = ArrayList(Constants.KWeekRowTwoDayCount)

            when (i) {
                0 -> {
                    L.d("addWeekRowTwoFdMondayData: 第1行-----------------------start")

                    // 第一行混合
                    val lastDay = lastCal.getActualMaximum(Calendar.DATE)
                    val curDayCount = if (firstWeekDay == FD_SUNDAY) {
                        Constants.KWeekRowOneDayCount - FD_SUNDAY
                    } else {
                        firstWeekDay - 2
                    }
                    addLastMonthDate(curDayCount, lastDay)
                    addCurMonthDate(Constants.KWeekRowTwoDayCount - curDayCount)

                    L.d("addWeekRowTwoFdMondayData: 第1行-----------------------end")
                }
                1 -> {
                    // 第二行全是当月
                    L.d("addWeekRowTwoFdMondayData: 第2行-----------------------start")
                    addCurMonthDate(Constants.KWeekRowTwoDayCount)
                    L.d("addWeekRowTwoFdMondayData: 第2行-----------------------end")
                }
                2 -> {
                    // 最后一行混合
                    L.d("addWeekRowTwoFdMondayData: 第3行-----------------------start")
                    val calDay = currUseDateIdx + Constants.KWeekRowTwoDayCount
                    val nextCount = calDay - currCalDates.size
                    val curCount = Constants.KWeekRowTwoDayCount - nextCount

                    addCurMonthDate(curCount)
                    addNextMonthDate(nextCount)
                    L.d("addWeekRowTwoFdMondayData: 第3行-----------------------end")
                }
                else -> {
                    throw IllegalStateException("没有第四行")
                }
            }

            perLineDays?.let { lines.add(it) }
        }
    }

    private fun addWeekRowTwoFdSundayData() {
        // 先计算1号是星期几
        // firstWeekDay 从1开始即周日
        val firstWeekDay = CalendarUtil.getFirstWeekDayOfMonth(currCal) + 1
        // TenLog.d(TAG, "firstWeekDay = " + firstWeekDay);

        for (i in 0 until Constants.KWeekRowTwoLineCount) {
            perLineDays = ArrayList(Constants.KWeekRowTwoDayCount)

            when (i) {
                0 -> {
                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------start");

                    val lastDay = lastCal.getActualMaximum(Calendar.DATE)
                    addLastMonthDate(firstWeekDay - 1, lastDay)

                    addCurMonthDate(Constants.KWeekRowTwoDayCount - firstWeekDay + 1)
                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------end");
                }
                1 -> {
                    addCurMonthDate(Constants.KWeekRowTwoDayCount)
                }
                2 -> {
                    val calDay = currUseDateIdx + Constants.KWeekRowTwoDayCount
                    // int size = currCalDates.size();
                    val nextCount = calDay - currCalDates.size
                    val curCount = Constants.KWeekRowTwoDayCount - nextCount

                    addCurMonthDate(curCount)
                    addNextMonthDate(nextCount)
                }
                else -> {
                }
            }

            perLineDays?.let { lines.add(it) }
        }
    }

    private fun addFirstDayMondayData() {
        // 先计算1号是星期几
        // firstWeekDay 从1开始即周日
        val firstWeekDay = CalendarUtil.getFirstWeekDayOfMonth(currCal) + 1

        for (i in 0 until Constants.KWeekRowOneLineCount) {
            perLineDays = ArrayList(Constants.KWeekRowOneDayCount)

            when (i) {
                0 -> {
                    /**
                     * 分两种情况
                     */
                    val lastDay = lastCal.getActualMaximum(Calendar.DATE)
                    when (firstWeekDay) {
                        Calendar.MONDAY -> {
                            // 第一行没有当月日期
                            addLastMonthDate(Constants.KWeekRowOneDayCount, lastDay)

                        }
                        Calendar.SUNDAY -> {
                            // 第一行当月只有周日
                            addLastMonthDate(Constants.KWeekRowOneDayCount - 1, lastDay)
                            addCurMonthDate(1)
                        }
                        else -> {
                            // 混合
                            addLastMonthDate(firstWeekDay - 2, lastDay)
                            addCurMonthDate(Constants.KWeekRowOneDayCount - (firstWeekDay - 2))
                        }
                    }

                    //                    YSLog.d(TAG, "第1行-----------------------end");
                }
                else -> {
                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------start");

                    val calDay = currUseDateIdx + Constants.KWeekRowOneDayCount
                    // int size = currCalDates.size();
                    val size = currCalDates.size
                    // MojiLog.d(TAG, "currCalDates.size() = " + size);
                    // TenLog.d(TAG, "calDay() = " + calDay);

                    if (calDay > size) {
                        // 混合
                        // int nextCount = calDay - currCalDates.size();
                        val nextCount = calDay - currCalDates.size
                        val curCount = Constants.KWeekRowOneDayCount - nextCount

                        addCurMonthDate(curCount)
                        addNextMonthDate(nextCount)

                    } else if (calDay <= size) {
                        // 只有当月
                        addCurMonthDate(Constants.KWeekRowOneDayCount)
                    }

                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------end");
                }
            }

            perLineDays?.let { lines.add(it) }
        }
    }

    private fun addFirstDaySundayData() {
        // 先计算1号是星期几
        // firstWeekDay 从1开始即周日
        val firstWeekDay = CalendarUtil.getFirstWeekDayOfMonth(currCal) + 1

        for (i in 0 until Constants.KWeekRowOneLineCount) {
            perLineDays = ArrayList(Constants.KWeekRowOneDayCount)

            when (i) {
                0 -> {
                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------start");

                    /**
                     * 分两种情况
                     */
                    if (firstWeekDay == Calendar.SUNDAY) {
                        // 第一行没有当月日期
                        val lastDay = lastCal.getActualMaximum(Calendar.DATE)
                        addLastMonthDate(Constants.KWeekRowOneDayCount, lastDay)

                    } else {
                        // 混合
                        val lastDay = lastCal.getActualMaximum(Calendar.DATE)
                        addLastMonthDate(firstWeekDay - 1, lastDay)

                        addCurMonthDate(Constants.KWeekRowOneDayCount - firstWeekDay + 1)
                    }

                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------end");
                }
                else -> {
                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------start");

                    val calDay = currUseDateIdx + Constants.KWeekRowOneDayCount
                    // int size = currCalDates.size();
                    val size = currCalDates.size

                    if (calDay > size) {
                        // 混合
                        // int nextCount = calDay - currCalDates.size();
                        val nextCount = calDay - currCalDates.size
                        val curCount = Constants.KWeekRowOneDayCount - nextCount

                        addCurMonthDate(curCount)
                        addNextMonthDate(nextCount)

                    } else if (calDay <= size) {
                        // 只有当月
                        addCurMonthDate(Constants.KWeekRowOneDayCount)
                    }

                    //                    YSLog.d(TAG, "第" + (i + 1) + "行-----------------------end");
                }
            }

            perLineDays?.let { lines.add(it) }
        }
    }

    fun getPerLineDays(index: Int): MutableList<Day>? {
        return if (index >= lines.size) {
            null
        } else lines[index]
    }


    /**
     * 添加某月剩余日期数据
     *
     * @param dayCount
     * @param lastDay
     */
    private fun addLastMonthDate(dayCount: Int, lastDay: Int) {

        for (j in 0 until dayCount) {

            val cal = lastCal.clone() as Calendar
            cal.set(Calendar.DATE, lastDay - dayCount + 1 + j)
            addPerLineDayData(cal, /* false, false */DayType.NOT_CUR)
        }
    }

    private fun addCurMonthDate(dayCount: Int) {
        for (i in 0 until dayCount) {

            val cal = currCal.clone() as Calendar

            cal.set(Calendar.DATE, currCalDates[currUseDateIdx])

            val weekDay = cal.get(Calendar.DAY_OF_WEEK)
            if (weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
                addPerLineDayData(cal, DayType.WEEKEND)
            } else {
                addPerLineDayData(cal, DayType.NORMAL)
            }
            currUseDateIdx++
        }
    }

    private fun addNextMonthDate(dayCount: Int) {
        for (j in 0 until dayCount) {

            val cal = nextCal.clone() as Calendar
            cal.set(Calendar.DATE, nextCalDates[nextUseDateIdx])

            val weekDay = cal.get(Calendar.DAY_OF_WEEK)
            if (weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
                addPerLineDayData(cal, /* false, true */DayType.NOT_CUR)
            } else {
                addPerLineDayData(cal, /* false, false */DayType.NOT_CUR)
            }
            nextUseDateIdx++
        }
    }

    private fun addPerLineDayData(cal: Calendar, type: DayType) {
        perLineDays?.add(Day(cal, type))
    }

    fun onDestroy() {
        perLineDays?.clear()
        lines.clear()
    }

}
