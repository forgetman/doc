package lib.base.util.festival

import android.text.TextUtils
import lib.base.R
import lib.base.util.LunarUtil
import lib.base.util.StringUtil
import logger.L
import vector.EMPTY
import vector.app.util.Res
import java.io.IOException
import java.text.ParseException
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.regex.Pattern
import kotlin.collections.LinkedHashMap


/**
 * 根据模板生成节日缓存
 * 暂时不改为kotlin
 *
 * @author 宋明超
 * @modify 元穗
 */
object FestivalUtil {

    private val GMT_TIMEZONE = TimeZone.getTimeZone("GMT")

    private val P = Pattern.compile("^(solar|lunar)\\((?:m(\\d+)):(ld|(?:d|(?:fw|lw|w(\\d+))n)(\\d+))\\)$")

    private val FESTIVAL_SPLIT = "|"

    private val FESTIVAL_SPLIT2 = ";"

    private val yearFestivalMap = LinkedHashMap<Int, LinkedHashMap<String, String>>()

    // 模板内容
    private var mTemplateContent: String? = null

    private val mTsdf = ThreadSafeSimpleDateFormat("yyyy/MM/dd", GMT_TIMEZONE)

    private val mRwlock = ReentrantReadWriteLock()

    private val readLock = mRwlock.readLock()

    private val writeLock = mRwlock.writeLock()

    init {
        readTemplateContent()
        val now = GregorianCalendar.getInstance(GMT_TIMEZONE)
        val curYear = now.get(Calendar.YEAR)

        // 需要初始化两年的节日, 年底需要预报跨年节日
        generateFestival(curYear, mTemplateContent)
        generateFestival(curYear + 1, mTemplateContent)
    }

    /**
     * 读取模板
     *
     * @throws IOException
     */
    fun readTemplateContent() {
        mTemplateContent = Res.getRaw(R.raw.festival).toString()
    }

    /**
     * 解析节日模板并生成节日
     *
     * @param templateContent
     */
    private fun generateFestival(year: Int, templateContent: String? = this.mTemplateContent) {
        val tokenizer = StringTokenizer(templateContent, "\r")

        val tmpFestivalMap = HashMap<Date, String?>()
        var festivalMap = yearFestivalMap[year]
        if (festivalMap == null) {
            festivalMap = LinkedHashMap()
            yearFestivalMap[year] = festivalMap
        } else {
            festivalMap.clear()
        }

        val festivalMapOfLunar = HashMap<String, String>()

        val cal = GregorianCalendar.getInstance(GMT_TIMEZONE)
        // 由于类似父亲节和母亲节的节日是按照xx星期xx星期日这种形式，所以推算一年中的第一个星期最少要有7天。并且一个星期的起始日是星期天
        cal.minimalDaysInFirstWeek = 7
        cal.firstDayOfWeek = Calendar.MONDAY // 需要设置周起始日为周一
        clearTime(cal)
        val lunarUtil = LunarUtil()

        // 生成阳历节日
        while (tokenizer.hasMoreTokens()) {
            var line = tokenizer.nextToken()
            cal.set(Calendar.YEAR, year)
            if (!TextUtils.isEmpty(line)) {
                line = line.trim { it <= ' ' }
                if (!line.startsWith("#")) {
                    val splits = line.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val m = P.matcher(splits[0])
                    if (m.matches()) {
                        val t = arrayOfNulls<String>(m.groupCount())
                        for (j in 1..m.groupCount()) {
                            m.start(j)
                            t[j - 1] = m.group(j)
                        }

                        val t1 = t[1] ?: EMPTY
                        val t2 = t[2] ?: EMPTY
                        val t3 = t[3] ?: EMPTY
                        val t4 = t[4] ?: EMPTY

                        var day = -1
                        var week = -1
                        var dayOfWeek = -1
                        var lastWeek = false
                        var lastDay = false
                        val calType = t[0]
                        val month = Integer.parseInt(t1)

                        if (t2.startsWith("d")) {
                            day = Integer.parseInt(t4)
                        } else {
                            if (t2 == "ld") {
                                lastDay = true
                            } else {
                                dayOfWeek = Integer.parseInt(t4)
                                when {
                                    t2.startsWith("lw") -> lastWeek = true
                                    t2.startsWith("fw") -> week = 1
                                    t2.startsWith("width") -> week = Integer.parseInt(t3)
                                }
                            }
                        }
                        if ("solar" == calType) {
                            cal.set(Calendar.MONTH, month - 1)
                            if (day != -1) {
                                cal.set(Calendar.DAY_OF_MONTH, day)
                            } else {
                                if (lastDay) {
                                    cal.set(
                                        Calendar.DAY_OF_MONTH,
                                        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                                    )
                                } else {
                                    if (!lastWeek) {
                                        cal.set(Calendar.WEEK_OF_MONTH, week)
                                        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                                    } else {
                                        week = cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
                                        cal.set(Calendar.WEEK_OF_MONTH, week)
                                        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                                    }
                                }

                                while (cal.get(Calendar.YEAR) < year || cal.get(Calendar.MONTH) < month - 1) { // deal
                                    // with
                                    // first
                                    // week
                                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                                }
                                while (cal.get(Calendar.YEAR) > year || cal.get(Calendar.MONTH) > month - 1) { // deal
                                    // with
                                    // last
                                    // week
                                    cal.set(Calendar.WEEK_OF_MONTH, -1)
                                }
                            }
                            // 处理清明节润年情况
                            if ((splits[1] == "清明节" || splits[1] == "寒食节") && (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)) {
                                cal.add(Calendar.DAY_OF_MONTH, -1)
                            }

                            // TenLog.d(TAG, "week = " + week);
                            // TenLog.d(TAG, "dayOfWeek = " + dayOfWeek);
                            // TenLog.d(TAG, "cal = " +
                            // cal.getTime().toString());

                            putValue(tmpFestivalMap, cal.time, splits[1])
                        } else if ("lunar" == calType) {
                            val key = month.toString() + "/" + if (lastDay) "ld" else day
                            festivalMapOfLunar[key] = splits[1]
                        }
                    }
                }
            }
        }

        // 生成阴历节日
        val excludeDateSet = HashSet<Date>()
        cal.set(year, 0, 1)
        while (cal.get(Calendar.YEAR) == year) {
            lunarUtil.setGregorian(
                year,
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            lunarUtil.computeChineseFields()
            // lunarUtil.computeSolarTerms();
            var lkey: String?
            var lf: String?
            // 阴历每月第一天，或阳历元旦
            if (lunarUtil.lunarYear == 1 || cal.get(Calendar.MONTH) == 0 && cal.get(Calendar.DAY_OF_MONTH) == 1) { // try
                // last
                // day
                lkey = lunarUtil.lunarYear.toString() + "/ld"
                lf = festivalMapOfLunar[lkey]
                if (!TextUtils.isEmpty(lf)) {
                    val tcal = GregorianCalendar.getInstance(GMT_TIMEZONE)
                    tcal.timeInMillis = cal.timeInMillis
                    tcal.add(
                        Calendar.DAY_OF_MONTH,
                        LunarUtil.daysInChineseMonth(
                            lunarUtil.lunarYear,
                            lunarUtil.lunarMonth
                        ) - lunarUtil.lunarDay
                    )
                    // 是同一年保留该节日，否则丢弃该节日
                    if (tcal.get(Calendar.YEAR) == year) {
                        putValue(tmpFestivalMap, tcal.time, lf)
                        excludeDateSet.add(tcal.time)
                    }
                }
            }

            // 如节日已计算过则直接跳过
            if (!excludeDateSet.contains(cal.time)) {
                lkey = lunarUtil.lunarMonth.toString() + "/" + lunarUtil.lunarDay
                lf = festivalMapOfLunar[lkey]
                if (!TextUtils.isEmpty(lf)) {
                    1
                    putValue(tmpFestivalMap, cal.time, lf)
                }
            }

            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val dateList = ArrayList(tmpFestivalMap.keys)
        L.d("dateList count = " + dateList.size)
        Collections.sort(dateList)
        L.d(year.toString() + "开始------------")
        for (i in dateList.indices) {
            val date = dateList[i]
            if (date != null) {
                festivalMap[mTsdf.format(date)] = tmpFestivalMap[date]!!
                // TenLog.d(TAG, tsdf.format(day) + "  " +
                // tmpFestivalMap.get(day));
            }
        }
        // for (Date day : dateList) {
        // festivalMap.put(tsdf2.format(day), tmpFestivalMap.get(day));
        // TenLog.d(/*"{}:{}", */tsdf.format(day), tmpFestivalMap.get(day));
        // }
        L.d(year.toString() + "结束------------")

        yearFestivalMap[year] = festivalMap
    }

    /**
     * 通过指定日期获取节日信息，当缓存里的节日的年份与请求的年份不符时，则丢弃缓存重新生成新的一年节日缓存
     *
     * @param date
     * @return
     */
    fun getName(date: String): String? {
        val year = Integer.parseInt(date.substring(0, 4))
        try {
            readLock.lock()
            if (yearFestivalMap.containsKey(year)) {
                return getName(year, date)
            }
        } finally {
            readLock.unlock()
        }

        try {
            writeLock.lock()

            if (!yearFestivalMap.containsKey(year)) {
                // // 假如新的是2014, 旧的是2012和13, 那么需要删除year - 2, 保留13, 14
                // // 假如新的是2011, 旧的是12和13, 那么需要删除year + 2, 保留11, 12
                if (yearFestivalMap.containsKey(year - 2)) {
                    // TenLog.d(TAG, "删除: " + (year - 2));
                    yearFestivalMap.remove(year - 2)
                } else {
                    // TenLog.d(TAG, "删除: " + (year + 2));
                    yearFestivalMap.remove(year + 2)
                }
                // 生成指定年的节日缓存
                generateFestival(year)
            }
            return getName(year, date)
        } finally {
            writeLock.unlock()
        }
    }

    /**
     * year, month, day 的取值范围遵循Calendar类的标准???
     *
     * @param year
     * @param month (1-12)
     * @param date
     * @return
     */
    fun getName(year: Int, month: Int, date: Int): String? {
        // TenLog.d(TAG, "StringUtil.leftPad = " +
        // StringUtil.leftPad(String.valueOf(month), 2, "0"));
        return getName(
            year.toString() + "/" + StringUtil.leftPad(month.toString(), 2, "0") + "/"
                    + StringUtil.leftPad(date.toString(), 2, "0")
        )
        // return null;
    }

    fun getName(cal: Calendar): String? {
        val format = mTsdf.format(cal.time)
        return getName(format)
    }

    private fun getName(year: Int, key: String): String? {
        return yearFestivalMap[year]?.get(key)
    }

    private fun putValue(map: MutableMap<Date, String?>, key: Date, value: String?) {
        var dateValue = map[key]
        if (!dateValue.isNullOrEmpty()) {
            dateValue += FESTIVAL_SPLIT + value
        } else {
            dateValue = value
        }
        map[key] = dateValue
    }

    private fun clearTime(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 返回有未来的1个节日
     *
     * @param aCal
     * @param dayOffset   偏移的天数
     * @param forecastDay 规定寻找的天数范围
     * @return 如果为null, 则表示在规定天数内没有节日
     * @throws ParseException
     */
    @Throws(ParseException::class)
    fun getForecastName(aCal: Calendar, dayOffset: Int, forecastDay: Int): String? {

        var festival: String? = null

        val cal = aCal.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)

        for (i in 1..forecastDay) {
            cal.add(Calendar.DAY_OF_YEAR, 1) // 不算当天
            festival = getName(cal)
            if (!TextUtils.isEmpty(festival)) {
                // 添加找到的节日
                festival += FESTIVAL_SPLIT2 + i
                break
            }
        }

        return festival
    }


}

