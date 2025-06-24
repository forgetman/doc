package lib.base.util

import lib.base.Constants
import java.util.*

/**
 * ChineseCalendarGB.java Copyright (c) 1997-2002 by Dr. Herong Yang 中国农历算法 -
 * 实用于公历 1901 年至 2100 年之间的 200 年
 */
class LunarUtil {

    //	public int rollUpOneDay() {
    //		dayOfWeek = dayOfWeek % 7 + 1;
    //		dayOfYear++;
    //		gregorianDate++;
    //		int days = daysInGregorianMonth(gregorianYear, gregorianMonth);
    //		if (gregorianDate > days) {
    //			gregorianDate = 1;
    //			gregorianMonth++;
    //			if (gregorianMonth > 12) {
    //				gregorianMonth = 1;
    //				gregorianYear++;
    //				dayOfYear = 1;
    //				isGregorianLeap = isGregorianLeapYear(gregorianYear);
    //			}
    //			sectionalTerm = sectionalTerm(gregorianYear, gregorianMonth);
    //			principleTerm = principleTerm(gregorianYear, gregorianMonth);
    //		}
    //		mLunarDate++;
    //		days = daysInChineseMonth(mLunarYear, mLunarMonth);
    //		if (mLunarDate > days) {
    //			mLunarDate = 1;
    //			mLunarMonth = nextChineseMonth(mLunarYear, mLunarMonth);
    //			if (mLunarMonth == 1)
    //				mLunarYear++;
    //		}
    //		return 0;
    //	}

    var gregorianYear: Int = 0
    var gregorianMonth: Int = 0
    var gregorianDate: Int = 0
    var isGregorianLeap: Boolean = false

    //	private int dayOfYear;
    //	private int dayOfWeek; // 周日一星期的第一天
    var lunarYear: Int = 0
        private set
    var lunarMonth: Int = 0
        private set // 负数表示闰月
    var lunarDay: Int = 0
        private set
    private var mSectionalTerm: Int = 0
    private var mPrincipleTerm: Int = 0

    /**
     * 获取年的天干地支
     *
     * @return
     */
    val chineseEraYearStr: String
        get() {
            val yearStem = (lunarYear - 1) % 10
            return KStemNames[yearStem] + KBranchNames[(lunarYear - 1) % 12] + KYearDefStr
        }

    val lunarMonthStr: String
        get() {
            var cd = lunarMonth
            if (cd < 1 || cd > 29) {
                cd = 1
            }

            return if (cd != 0) {
                KMonthOfAlmanac[cd - 1]
            } else {
                ""
            }
        }

    val lunarDayStr: String
        get() = if (lunarDay != 0) {
            KDaysOfAlmanac[lunarDay - 1]
        } else {
            ""
        }

    val solarTerm: String
        get() {
            var index = gregorianMonth - 2
            if (index < 0) {
                index = 11
            }

            var solarStr: String? = null
            if (gregorianDate == mSectionalTerm) {
                solarStr = KSectionalTermNames[index]
            } else if (gregorianDate == mPrincipleTerm) {
                solarStr = KPrincipleTermNames[index]
            } else {
                solarStr = ""
            }

            return solarStr
        }

    /**
     * 返回"闰"或""
     */
    val lunarPrefix: String
        get() = if (isGregorianLeap) KLunarRunStr else ""

    val dateString: String
        get() {
            var str = "*  /  "
            var gm = gregorianMonth.toString()
            if (gm.length == 1) {
                gm = " $gm"
            }
            var cm = Math.abs(lunarMonth).toString()
            if (cm.length == 1) {
                cm = " $cm"
            }
            var gd = gregorianDate.toString()
            if (gd.length == 1) {
                gd = " $gd"
            }
            var cd = lunarDay.toString()
            if (cd.length == 1) {
                cd = " $cd"
            }
            if (gregorianDate == mSectionalTerm) {
                str = " " + KSectionalTermNames[gregorianMonth - 1]
            } else if (gregorianDate == mPrincipleTerm) {
                str = " " + KPrincipleTermNames[gregorianMonth - 1]
            } else if (lunarDay == 1 && lunarMonth > 0) {
                str = " " + chineseMonthNames[lunarMonth - 1] + "月"
            } else if (lunarDay == 1 && lunarMonth < 0) {
                str = "*" + chineseMonthNames[-lunarMonth - 1] + "月"
            } else {
                str = "$gd/$cd"
            }
            return str
        }

    /*
     * 获取年月日组合: 2013年9月5日
     */
    val gregorianStr: String
        get() = gregorianYear.toString() + KYearDefStr + gregorianMonth + KMonthDefStr + gregorianDate + KDayDefStr

    constructor() {
        setGregorian(Constants.KLunarEndFirst, 1, 1)
    }

    constructor(y: Int, m: Int, d: Int) {
        setGregorian(y, m, d)
        computeChineseFields()
        computeSolarTerms()
    }

    constructor(cal: Calendar) {
        val y: Int
        val m: Int
        val d: Int
        y = cal.get(Calendar.YEAR)
        m = cal.get(Calendar.MONTH) + 1
        d = cal.get(Calendar.DATE)

        setGregorian(y, m, d)
        computeChineseFields()
        computeSolarTerms()
    }

    fun getNextSolarTermDayDiff(date: Date): Int {

        val cal = Calendar.getInstance()
        cal.setTime(date)

        var dayDiff = 0
        dayDiff = 0
        while (dayDiff < 365) {
            if (getSolarStr(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DATE)
                ).length != 0
            ) {
                break
            }
            cal.add(Calendar.DATE, 1)
            ++dayDiff
        }

        return dayDiff
    }

    fun getNextSolarTermDesc(date: Date): String {
        val cal = Calendar.getInstance()
        cal.setTime(date)

        var solar: String
        for (i in 0..364) {

            solar = getSolarStr(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DATE)
            )

            if (solar.length != 0) {
                return solar
            }
            cal.add(Calendar.DATE, 1)
        }

        return ""
    }

    private fun getSolarStr(year: Int, month: Int, day: Int): String {

        var solar = ""

        var index = month - 2
        if (index < 0) {
            index = 11
        }

        val sectionalTerm = sectionalTerm(year, month)
        val principleTerm = principleTerm(year, month)

        if (day == sectionalTerm) {
            solar = KSectionalTermNames[index]
        } else if (day == principleTerm) {
            solar = KPrincipleTermNames[index]
        }

        return solar
    }


    /**
     * 得到对应天的农历 要判断闰月 月初 月末 *
     *
     * @param y
     * @param m
     * @param d
     * @return String
     */
    //	public String getChineseDay(int y, int m, int d) {
    //
    //		LunarUtil c = new LunarUtil();
    //
    //		c.setGregorian(y, m, d);
    //		c.computeChineseFields();
    //		c.computeSolarTerms();
    //
    //		int cd = c.getChineseDate();
    //
    //		return daysOfAlmanac[cd - 1];
    //	}

    /**
     * 得到对应天的农历 要判断闰月 月初 月末
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    //	public String getChineseMonth(int y, int m, int d) {
    //		setGregorian(y, m, d);
    //		computeChineseFields();
    //		computeSolarTerms();
    //
    //		int cd = mLunarMonth;
    //		if (cd < 1 || cd > 29)
    //			cd = 1;
    //		return monthOfAlmanac[cd - 1];
    //	}
    fun setGregorian(y: Int, m: Int, d: Int) {
        gregorianYear = y
        gregorianMonth = m
        gregorianDate = d
        isGregorianLeap = isLeapYear(y)
        //		dayOfYear = dayOfYear(y, m, d);
        //		dayOfWeek = dayOfWeek(y, m, d);
        lunarYear = 0
        lunarMonth = 0
        lunarDay = 0
        mSectionalTerm = 0
        mPrincipleTerm = 0
    }

    fun computeChineseFields(): Int {
        if (gregorianYear < Constants.KLunarEndFirst || gregorianYear > Constants.KLunarEndLast) {
            return 1
        }
        var startYear = baseYear
        var startMonth = baseMonth
        var startDate = baseDate
        lunarYear = baseChineseYear
        lunarMonth = baseChineseMonth
        lunarDay = baseChineseDate
        // 第二个对应日，用以提高计算效率
        // 公历 2000 年 1 月 1 日，对应农历 4697 年 11 月 25 日
        if (gregorianYear >= 2000) {
            startYear = baseYear + 99
            startMonth = 1
            startDate = 1
            lunarYear = baseChineseYear + 99
            lunarMonth = 11
            lunarDay = 25
        }
        var daysDiff = 0
        for (i in startYear until gregorianYear) {
            daysDiff += 365
            if (isLeapYear(i)) {
                daysDiff += 1 // leap year
            }
        }
        for (i in startMonth until gregorianMonth) {
            daysDiff += daysInGregorianMonth(gregorianYear, i)
        }
        daysDiff += gregorianDate - startDate

        lunarDay += daysDiff
        var lastDate = daysInChineseMonth(lunarYear, lunarMonth)
        var nextMonth = nextChineseMonth(lunarYear, lunarMonth)
        while (lunarDay > lastDate) {
            if (Math.abs(nextMonth) < Math.abs(lunarMonth)) {
                lunarYear++
            }
            lunarMonth = nextMonth
            lunarDay -= lastDate
            lastDate = daysInChineseMonth(lunarYear, lunarMonth)
            nextMonth = nextChineseMonth(lunarYear, lunarMonth)
        }
        return 0
    }

    fun computeSolarTerms(): Int {
        if (gregorianYear < Constants.KLunarEndFirst || gregorianYear > Constants.KLunarEndLast) {
            return 1
        }
        mSectionalTerm = sectionalTerm(gregorianYear, gregorianMonth)
        mPrincipleTerm = principleTerm(gregorianYear, gregorianMonth)
        return 0
    }

    override fun toString(): String {
        val buf = StringBuffer()
        //		buf.append("Gregorian Year: " + mYear + "\n");
        //		buf.append("Gregorian Month: " + mMonth + "\n");
        //		buf.append("Gregorian Date: " + mDay + "\n");
        //		buf.append("Is Leap Year: " + isGregorianLeap + "\n");
        buf.append("$gregorianYear $gregorianMonth $gregorianDate\n")
        //		buf.append("Day of Year: " + dayOfYear + "\n");
        //		buf.append("Day of Week: " + dayOfWeek + "\n");
        buf.append("Lunar Year: $lunarYear\n")
        buf.append("Lunar Month: $lunarMonth\n")
        val yearStem = (lunarYear - 1) % 10
        //		buf.append("年 天干 下标: " + yearStem + "\n");
        buf.append("年 天干地支: " + KStemNames[yearStem] + KBranchNames[(lunarYear - 1) % 12] + "\n")
        //		buf.append("年 地支: " + KBranchNames[((mLunarYear - 1) % 12)] + "\n");

        /**
         * 月干公式、月的地支是固定的如正月起寅之类，只计算月干
         * 　　			月干=年干数×2+月份
         */
        var cd = lunarMonth
        if (cd < 1 || cd > 29) {
            cd = 1
        }
        var monthStem = ((yearStem + 1) * 2 + cd) % 10 - 1

        if (monthStem < 0) {
            monthStem += 10
        }

        buf.append("月 天干地支: " + KStemNames[monthStem] + KMonthBranchNames[cd - 1] + "\n")

        // G = 4C + [C / 4] + 5y + [y / 4] + [3 * (M + 1) / 5] + d - 3
        // Z = 8C + [C / 4] + 5y + [y / 4] + [3 * (M + 1) / 5] + d + 7 + i
        // C 是世纪数减一，y 是年份后两位，M 是月份，d 是日数。1月和2月按上一年的13月和14月来算。奇数月i=0，偶数月i=6。G 除以10的余数是天干，Z 除以12的余数是地支。

        val century: Int
        if (gregorianYear >= 2001) {
            century = 21 - 1
        } else {
            century = 20 - 1
        }

        val useMonth: Int
        val yearBase: Int
        if (gregorianMonth == 1) {
            useMonth = 13
            yearBase = (gregorianYear - 1) % 100
        } else if (gregorianMonth == 2) {
            useMonth = 14
            yearBase = (gregorianYear - 1) % 100
        } else {
            useMonth = gregorianMonth
            yearBase = gregorianYear % 100
        }

        var dayStem =
            4 * century + century / 4 + 5 * yearBase + yearBase / 4 + 3 * (useMonth + 1) / 5 + gregorianDate - 3
        dayStem %= 10
        if (dayStem <= 0) {
            dayStem = 10
        }

        val i = if (gregorianMonth % 2 == 0) 6 else 0
        var dayBranch =
            8 * century + century / 4 + 5 * yearBase + yearBase / 4 + 3 * (useMonth + 1) / 5 + gregorianDate + 7 + i
        dayBranch %= 12
        if (dayBranch <= 0) {
            dayBranch = 12
        }

        buf.append("日 天干地支: " + KStemNames[dayStem - 1] + KBranchNames[dayBranch - 1])


        //		buf.append("Chinese Month: " + mLunarMonth + "\n");
        //		buf.append("Chinese Date: " + mLunarDate + "\n");
        //		buf.append("Sectional Term: " + sectionalTerm + "\n");
        //		buf.append("Principle Term: " + principleTerm + "\n");
        return buf.toString()
    }

    companion object {

        private val KLunarRunStr = "闰"
        private val KDaysInGregorianMonth = charArrayOf(
            31.toChar(),
            28.toChar(),
            31.toChar(),
            30.toChar(),
            31.toChar(),
            30.toChar(),
            31.toChar(),
            31.toChar(),
            30.toChar(),
            31.toChar(),
            30.toChar(),
            31.toChar()
        )
        private val KStemNames = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
        private val KBranchNames =
            arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
        private val KMonthBranchNames =
            arrayOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")

        val KYearDefStr = "年"
        val KMonthDefStr = "月"
        val KDayDefStr = "日"

        //	private static final String[] animalNames = { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

        //	public static final String[] daysOfMonth = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
        //			"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };

        private val KMonthOfAlmanac =
            arrayOf("正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月")
        private val KDaysOfAlmanac = arrayOf(
            "初一",
            "初二",
            "初三",
            "初四",
            "初五",
            "初六",
            "初七",
            "初八",
            "初九",
            "初十",
            "十一",
            "十二",
            "十三",
            "十四",
            "十五",
            "十六",
            "十七",
            "十八",
            "十九",
            "二十",
            "廿一",
            "廿二",
            "廿三",
            "廿四",
            "廿五",
            "廿六",
            "廿七",
            "廿八",
            "廿九",
            "三十"
        ) // 农历的天数

        // 判断是否是闰年
        fun isLeapYear(year: Int): Boolean {
            var isLeap = false
            if (year % 4 == 0) {
                isLeap = true
            }
            if (year % 100 == 0) {
                isLeap = false
            }
            if (year % 400 == 0) {
                isLeap = true
            }
            return isLeap
        }

        // 返回一个月有几天
        fun daysInGregorianMonth(y: Int, m: Int): Int {
            var d = KDaysInGregorianMonth[m - 1].code
            if (m == 2 && isLeapYear(y)) {
                d++ // 公历闰年二月多一天
            }
            return d
        }

        // 计算当前天在本年中是第几天
        fun dayOfYear(y: Int, m: Int, d: Int): Int {
            var c = 0
            for (i in 1 until m) {
                c = c + daysInGregorianMonth(y, i)
            }
            c = c + d
            return c
        }

        // 当前天是本周的第几天 ， 从星期天开始算
        fun dayOfWeek(y: Int, m: Int, d: Int): Int {
            var y = y
            var w = 1 // 公历一年一月一日是星期一，所以起始值为星期日
            y = (y - 1) % 400 + 1 // 公历星期值分部 400 年循环一次
            var ly = (y - 1) / 4 // 闰年次数
            ly = ly - (y - 1) / 100
            ly = ly + (y - 1) / 400
            val ry = y - 1 - ly // 常年次数
            w = w + ry // 常年星期值增一
            w = w + 2 * ly // 闰年星期值增二
            w = w + dayOfYear(y, m, d)
            w = (w - 1) % 7 + 1
            return w
        }

        // 农历月份大小压缩表，两个字节表示一年。两个字节共十六个二进制位数，
        // 前四个位数表示闰月月份，后十二个位数表示十二个农历月份的大小。
        private val KChineseMonths = charArrayOf(
            0x00.toChar(),
            0x04.toChar(),
            0xad.toChar(),
            0x08.toChar(),
            0x5a.toChar(),
            0x01.toChar(),
            0xd5.toChar(),
            0x54.toChar(),
            0xb4.toChar(),
            0x09.toChar(),
            0x64.toChar(),
            0x05.toChar(),
            0x59.toChar(),
            0x45.toChar(),
            0x95.toChar(),
            0x0a.toChar(),
            0xa6.toChar(),
            0x04.toChar(),
            0x55.toChar(),
            0x24.toChar(),
            0xad.toChar(),
            0x08.toChar(),
            0x5a.toChar(),
            0x62.toChar(),
            0xda.toChar(),
            0x04.toChar(),
            0xb4.toChar(),
            0x05.toChar(),
            0xb4.toChar(),
            0x55.toChar(),
            0x52.toChar(),
            0x0d.toChar(),
            0x94.toChar(),
            0x0a.toChar(),
            0x4a.toChar(),
            0x2a.toChar(),
            0x56.toChar(),
            0x02.toChar(),
            0x6d.toChar(),
            0x71.toChar(),
            0x6d.toChar(),
            0x01.toChar(),
            0xda.toChar(),
            0x02.toChar(),
            0xd2.toChar(),
            0x52.toChar(),
            0xa9.toChar(),
            0x05.toChar(),
            0x49.toChar(),
            0x0d.toChar(),
            0x2a.toChar(),
            0x45.toChar(),
            0x2b.toChar(),
            0x09.toChar(),
            0x56.toChar(),
            0x01.toChar(),
            0xb5.toChar(),
            0x20.toChar(),
            0x6d.toChar(),
            0x01.toChar(),
            0x59.toChar(),
            0x69.toChar(),
            0xd4.toChar(),
            0x0a.toChar(),
            0xa8.toChar(),
            0x05.toChar(),
            0xa9.toChar(),
            0x56.toChar(),
            0xa5.toChar(),
            0x04.toChar(),
            0x2b.toChar(),
            0x09.toChar(),
            0x9e.toChar(),
            0x38.toChar(),
            0xb6.toChar(),
            0x08.toChar(),
            0xec.toChar(),
            0x74.toChar(),
            0x6c.toChar(),
            0x05.toChar(),
            0xd4.toChar(),
            0x0a.toChar(),
            0xe4.toChar(),
            0x6a.toChar(),
            0x52.toChar(),
            0x05.toChar(),
            0x95.toChar(),
            0x0a.toChar(),
            0x5a.toChar(),
            0x42.toChar(),
            0x5b.toChar(),
            0x04.toChar(),
            0xb6.toChar(),
            0x04.toChar(),
            0xb4.toChar(),
            0x22.toChar(),
            0x6a.toChar(),
            0x05.toChar(),
            0x52.toChar(),
            0x75.toChar(),
            0xc9.toChar(),
            0x0a.toChar(),
            0x52.toChar(),
            0x05.toChar(),
            0x35.toChar(),
            0x55.toChar(),
            0x4d.toChar(),
            0x0a.toChar(),
            0x5a.toChar(),
            0x02.toChar(),
            0x5d.toChar(),
            0x31.toChar(),
            0xb5.toChar(),
            0x02.toChar(),
            0x6a.toChar(),
            0x8a.toChar(),
            0x68.toChar(),
            0x05.toChar(),
            0xa9.toChar(),
            0x0a.toChar(),
            0x8a.toChar(),
            0x6a.toChar(),
            0x2a.toChar(),
            0x05.toChar(),
            0x2d.toChar(),
            0x09.toChar(),
            0xaa.toChar(),
            0x48.toChar(),
            0x5a.toChar(),
            0x01.toChar(),
            0xb5.toChar(),
            0x09.toChar(),
            0xb0.toChar(),
            0x39.toChar(),
            0x64.toChar(),
            0x05.toChar(),
            0x25.toChar(),
            0x75.toChar(),
            0x95.toChar(),
            0x0a.toChar(),
            0x96.toChar(),
            0x04.toChar(),
            0x4d.toChar(),
            0x54.toChar(),
            0xad.toChar(),
            0x04.toChar(),
            0xda.toChar(),
            0x04.toChar(),
            0xd4.toChar(),
            0x44.toChar(),
            0xb4.toChar(),
            0x05.toChar(),
            0x54.toChar(),
            0x85.toChar(),
            0x52.toChar(),
            0x0d.toChar(),
            0x92.toChar(),
            0x0a.toChar(),
            0x56.toChar(),
            0x6a.toChar(),
            0x56.toChar(),
            0x02.toChar(),
            0x6d.toChar(),
            0x02.toChar(),
            0x6a.toChar(),
            0x41.toChar(),
            0xda.toChar(),
            0x02.toChar(),
            0xb2.toChar(),
            0xa1.toChar(),
            0xa9.toChar(),
            0x05.toChar(),
            0x49.toChar(),
            0x0d.toChar(),
            0x0a.toChar(),
            0x6d.toChar(),
            0x2a.toChar(),
            0x09.toChar(),
            0x56.toChar(),
            0x01.toChar(),
            0xad.toChar(),
            0x50.toChar(),
            0x6d.toChar(),
            0x01.toChar(),
            0xd9.toChar(),
            0x02.toChar(),
            0xd1.toChar(),
            0x3a.toChar(),
            0xa8.toChar(),
            0x05.toChar(),
            0x29.toChar(),
            0x85.toChar(),
            0xa5.toChar(),
            0x0c.toChar(),
            0x2a.toChar(),
            0x09.toChar(),
            0x96.toChar(),
            0x54.toChar(),
            0xb6.toChar(),
            0x08.toChar(),
            0x6c.toChar(),
            0x09.toChar(),
            0x64.toChar(),
            0x45.toChar(),
            0xd4.toChar(),
            0x0a.toChar(),
            0xa4.toChar(),
            0x05.toChar(),
            0x51.toChar(),
            0x25.toChar(),
            0x95.toChar(),
            0x0a.toChar(),
            0x2a.toChar(),
            0x72.toChar(),
            0x5b.toChar(),
            0x04.toChar(),
            0xb6.toChar(),
            0x04.toChar(),
            0xac.toChar(),
            0x52.toChar(),
            0x6a.toChar(),
            0x05.toChar(),
            0xd2.toChar(),
            0x0a.toChar(),
            0xa2.toChar(),
            0x4a.toChar(),
            0x4a.toChar(),
            0x05.toChar(),
            0x55.toChar(),
            0x94.toChar(),
            0x2d.toChar(),
            0x0a.toChar(),
            0x5a.toChar(),
            0x02.toChar(),
            0x75.toChar(),
            0x61.toChar(),
            0xb5.toChar(),
            0x02.toChar(),
            0x6a.toChar(),
            0x03.toChar(),
            0x61.toChar(),
            0x45.toChar(),
            0xa9.toChar(),
            0x0a.toChar(),
            0x4a.toChar(),
            0x05.toChar(),
            0x25.toChar(),
            0x25.toChar(),
            0x2d.toChar(),
            0x09.toChar(),
            0x9a.toChar(),
            0x68.toChar(),
            0xda.toChar(),
            0x08.toChar(),
            0xb4.toChar(),
            0x09.toChar(),
            0xa8.toChar(),
            0x59.toChar(),
            0x54.toChar(),
            0x03.toChar(),
            0xa5.toChar(),
            0x0a.toChar(),
            0x91.toChar(),
            0x3a.toChar(),
            0x96.toChar(),
            0x04.toChar(),
            0xad.toChar(),
            0xb0.toChar(),
            0xad.toChar(),
            0x04.toChar(),
            0xda.toChar(),
            0x04.toChar(),
            0xf4.toChar(),
            0x62.toChar(),
            0xb4.toChar(),
            0x05.toChar(),
            0x54.toChar(),
            0x0b.toChar(),
            0x44.toChar(),
            0x5d.toChar(),
            0x52.toChar(),
            0x0a.toChar(),
            0x95.toChar(),
            0x04.toChar(),
            0x55.toChar(),
            0x22.toChar(),
            0x6d.toChar(),
            0x02.toChar(),
            0x5a.toChar(),
            0x71.toChar(),
            0xda.toChar(),
            0x02.toChar(),
            0xaa.toChar(),
            0x05.toChar(),
            0xb2.toChar(),
            0x55.toChar(),
            0x49.toChar(),
            0x0b.toChar(),
            0x4a.toChar(),
            0x0a.toChar(),
            0x2d.toChar(),
            0x39.toChar(),
            0x36.toChar(),
            0x01.toChar(),
            0x6d.toChar(),
            0x80.toChar(),
            0x6d.toChar(),
            0x01.toChar(),
            0xd9.toChar(),
            0x02.toChar(),
            0xe9.toChar(),
            0x6a.toChar(),
            0xa8.toChar(),
            0x05.toChar(),
            0x29.toChar(),
            0x0b.toChar(),
            0x9a.toChar(),
            0x4c.toChar(),
            0xaa.toChar(),
            0x08.toChar(),
            0xb6.toChar(),
            0x08.toChar(),
            0xb4.toChar(),
            0x38.toChar(),
            0x6c.toChar(),
            0x09.toChar(),
            0x54.toChar(),
            0x75.toChar(),
            0xd4.toChar(),
            0x0a.toChar(),
            0xa4.toChar(),
            0x05.toChar(),
            0x45.toChar(),
            0x55.toChar(),
            0x95.toChar(),
            0x0a.toChar(),
            0x9a.toChar(),
            0x04.toChar(),
            0x55.toChar(),
            0x44.toChar(),
            0xb5.toChar(),
            0x04.toChar(),
            0x6a.toChar(),
            0x82.toChar(),
            0x6a.toChar(),
            0x05.toChar(),
            0xd2.toChar(),
            0x0a.toChar(),
            0x92.toChar(),
            0x6a.toChar(),
            0x4a.toChar(),
            0x05.toChar(),
            0x55.toChar(),
            0x0a.toChar(),
            0x2a.toChar(),
            0x4a.toChar(),
            0x5a.toChar(),
            0x02.toChar(),
            0xb5.toChar(),
            0x02.toChar(),
            0xb2.toChar(),
            0x31.toChar(),
            0x69.toChar(),
            0x03.toChar(),
            0x31.toChar(),
            0x73.toChar(),
            0xa9.toChar(),
            0x0a.toChar(),
            0x4a.toChar(),
            0x05.toChar(),
            0x2d.toChar(),
            0x55.toChar(),
            0x2d.toChar(),
            0x09.toChar(),
            0x5a.toChar(),
            0x01.toChar(),
            0xd5.toChar(),
            0x48.toChar(),
            0xb4.toChar(),
            0x09.toChar(),
            0x68.toChar(),
            0x89.toChar(),
            0x54.toChar(),
            0x0b.toChar(),
            0xa4.toChar(),
            0x0a.toChar(),
            0xa5.toChar(),
            0x6a.toChar(),
            0x95.toChar(),
            0x04.toChar(),
            0xad.toChar(),
            0x08.toChar(),
            0x6a.toChar(),
            0x44.toChar(),
            0xda.toChar(),
            0x04.toChar(),
            0x74.toChar(),
            0x05.toChar(),
            0xb0.toChar(),
            0x25.toChar(),
            0x54.toChar(),
            0x03.toChar()
        )

        // 初始日，公历农历对应日期：
        // 公历 1901 年 1 月 1 日，对应农历 4598 年 11 月 11 日
        private val baseYear = Constants.KLunarEndFirst
        private val baseMonth = 1
        private val baseDate = 1
        private val baseIndex = 0
        private val baseChineseYear = 4598 - 1
        private val baseChineseMonth = 11
        private val baseChineseDate = 11

        private val KBigLeapMonthYears = intArrayOf(
            // 大闰月的闰年年份
            6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150, 155, 158, 185, 193
        )

        fun daysInChineseMonth(y: Int, m: Int): Int {
            // 注意：闰月 m < 0
            val index = y - baseChineseYear + baseIndex
            var v = 0
            var l = 0
            var d = 30
            if (1 <= m && m <= 8) {
                v = KChineseMonths[2 * index].code
                l = m - 1
                if (v shr l and 0x01 == 1) {
                    d = 29
                }
            } else if (9 <= m && m <= 12) {
                v = KChineseMonths[2 * index + 1].code
                l = m - 9
                if (v shr l and 0x01 == 1) {
                    d = 29
                }
            } else {
                v = KChineseMonths[2 * index + 1].code
                v = v shr 4 and 0x0F
                if (v != Math.abs(m)) {
                    d = 0
                } else {
                    d = 29
                    for (i in KBigLeapMonthYears.indices) {
                        if (KBigLeapMonthYears[i] == index) {
                            d = 30
                            break
                        }
                    }
                }
            }
            return d
        }

        fun nextChineseMonth(y: Int, m: Int): Int {
            var n = Math.abs(m) + 1
            if (m > 0) {
                val index = y - baseChineseYear + baseIndex
                var v = KChineseMonths[2 * index + 1].code
                v = v shr 4 and 0x0F
                if (v == m) {
                    n = -m
                }
            }
            if (n == 13) {
                n = 1
            }
            return n
        }

        private val KSectionalTermMap = arrayOf(
            charArrayOf(
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar()
            ),
            charArrayOf(
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                3.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                3.toChar(),
                3.toChar(),
                4.toChar(),
                4.toChar(),
                3.toChar(),
                3.toChar(),
                3.toChar()
            ),
            charArrayOf(
                6.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar()
            ),
            charArrayOf(
                5.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                5.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                4.toChar(),
                5.toChar()
            ),
            charArrayOf(
                6.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar()
            ),
            charArrayOf(
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                6.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                4.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar(),
                5.toChar()
            ),
            charArrayOf(
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar()
            ),
            charArrayOf(
                8.toChar(),
                8.toChar(),
                8.toChar(),
                9.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar()
            ),
            charArrayOf(
                8.toChar(),
                8.toChar(),
                8.toChar(),
                9.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar()
            ),
            charArrayOf(
                9.toChar(),
                9.toChar(),
                9.toChar(),
                9.toChar(),
                8.toChar(),
                9.toChar(),
                9.toChar(),
                9.toChar(),
                8.toChar(),
                8.toChar(),
                9.toChar(),
                9.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                9.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar()
            ),
            charArrayOf(
                8.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar()
            ),
            charArrayOf(
                7.toChar(),
                8.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                8.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar(),
                6.toChar(),
                6.toChar(),
                6.toChar(),
                7.toChar(),
                7.toChar()
            )
        )
        private val KSectionalTermYear = arrayOf(
            charArrayOf(
                13.toChar(),
                49.toChar(),
                85.toChar(),
                117.toChar(),
                149.toChar(),
                185.toChar(),
                201.toChar(),
                250.toChar(),
                250.toChar()
            ),
            charArrayOf(
                13.toChar(),
                45.toChar(),
                81.toChar(),
                117.toChar(),
                149.toChar(),
                185.toChar(),
                201.toChar(),
                250.toChar(),
                250.toChar()
            ),
            charArrayOf(
                13.toChar(),
                48.toChar(),
                84.toChar(),
                112.toChar(),
                148.toChar(),
                184.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                13.toChar(),
                45.toChar(),
                76.toChar(),
                108.toChar(),
                140.toChar(),
                172.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                13.toChar(),
                44.toChar(),
                72.toChar(),
                104.toChar(),
                132.toChar(),
                168.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                5.toChar(),
                33.toChar(),
                68.toChar(),
                96.toChar(),
                124.toChar(),
                152.toChar(),
                188.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                29.toChar(),
                57.toChar(),
                85.toChar(),
                120.toChar(),
                148.toChar(),
                176.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                13.toChar(),
                48.toChar(),
                76.toChar(),
                104.toChar(),
                132.toChar(),
                168.toChar(),
                196.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                25.toChar(),
                60.toChar(),
                88.toChar(),
                120.toChar(),
                148.toChar(),
                184.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                16.toChar(),
                44.toChar(),
                76.toChar(),
                108.toChar(),
                144.toChar(),
                172.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                28.toChar(),
                60.toChar(),
                92.toChar(),
                124.toChar(),
                160.toChar(),
                192.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            ),
            charArrayOf(
                17.toChar(),
                53.toChar(),
                85.toChar(),
                124.toChar(),
                156.toChar(),
                188.toChar(),
                200.toChar(),
                201.toChar(),
                250.toChar()
            )
        )
        private val KPrincipleTermMap = arrayOf(
            charArrayOf(
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                19.toChar(),
                20.toChar()
            ),
            charArrayOf(
                20.toChar(),
                19.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                18.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                18.toChar(),
                18.toChar(),
                19.toChar(),
                19.toChar(),
                18.toChar(),
                18.toChar(),
                18.toChar(),
                18.toChar(),
                18.toChar(),
                18.toChar(),
                18.toChar()
            ),
            charArrayOf(
                21.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar()
            ),
            charArrayOf(
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar(),
                19.toChar(),
                19.toChar(),
                19.toChar(),
                20.toChar(),
                20.toChar()
            ),
            charArrayOf(
                21.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar()
            ),
            charArrayOf(
                22.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                20.toChar(),
                20.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar()
            ),
            charArrayOf(
                23.toChar(),
                23.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar()
            ),
            charArrayOf(
                23.toChar(),
                24.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar()
            ),
            charArrayOf(
                23.toChar(),
                24.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar()
            ),
            charArrayOf(
                24.toChar(),
                24.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                24.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                24.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar()
            ),
            charArrayOf(
                23.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar()
            ),
            charArrayOf(
                22.toChar(),
                22.toChar(),
                23.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                23.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                21.toChar(),
                22.toChar()
            )
        )
        private val KPrincipleTermYear = arrayOf(
            charArrayOf(
                13.toChar(),
                45.toChar(),
                81.toChar(),
                113.toChar(),
                149.toChar(),
                185.toChar(),
                201.toChar()
            ),
            charArrayOf(
                21.toChar(),
                57.toChar(),
                93.toChar(),
                125.toChar(),
                161.toChar(),
                193.toChar(),
                201.toChar()
            ),
            charArrayOf(
                21.toChar(),
                56.toChar(),
                88.toChar(),
                120.toChar(),
                152.toChar(),
                188.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                21.toChar(),
                49.toChar(),
                81.toChar(),
                116.toChar(),
                144.toChar(),
                176.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                17.toChar(),
                49.toChar(),
                77.toChar(),
                112.toChar(),
                140.toChar(),
                168.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                28.toChar(),
                60.toChar(),
                88.toChar(),
                116.toChar(),
                148.toChar(),
                180.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                25.toChar(),
                53.toChar(),
                84.toChar(),
                112.toChar(),
                144.toChar(),
                172.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                29.toChar(),
                57.toChar(),
                89.toChar(),
                120.toChar(),
                148.toChar(),
                180.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                17.toChar(),
                45.toChar(),
                73.toChar(),
                108.toChar(),
                140.toChar(),
                168.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                28.toChar(),
                60.toChar(),
                92.toChar(),
                124.toChar(),
                160.toChar(),
                192.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                16.toChar(),
                44.toChar(),
                80.toChar(),
                112.toChar(),
                148.toChar(),
                180.toChar(),
                200.toChar(),
                201.toChar()
            ),
            charArrayOf(
                17.toChar(),
                53.toChar(),
                88.toChar(),
                120.toChar(),
                156.toChar(),
                188.toChar(),
                200.toChar(),
                201.toChar()
            )
        )

        fun sectionalTerm(y: Int, m: Int): Int {
            if (y < Constants.KLunarEndFirst || y > Constants.KLunarEndLast) {
                return 0
            }
            var index = 0
            val ry = y - baseYear + 1
            while (ry >= KSectionalTermYear[m - 1][index].toInt()) {
                index++
            }
            var term = KSectionalTermMap[m - 1][4 * index + ry % 4].code
            if (ry == 121 && m == 4) {
                term = 5
            }
            if (ry == 132 && m == 4) {
                term = 5
            }
            if (ry == 194 && m == 6) {
                term = 6
            }
            return term
        }

        fun principleTerm(y: Int, m: Int): Int {
            if (y < Constants.KLunarEndFirst || y > Constants.KLunarEndLast) {
                return 0
            }
            var index = 0
            val ry = y - baseYear + 1
            while (ry >= KPrincipleTermYear[m - 1][index].toInt()) {
                index++
            }
            var term = KPrincipleTermMap[m - 1][4 * index + ry % 4].toInt()
            if (ry == 171 && m == 3) {
                term = 21
            }
            if (ry == 181 && m == 5) {
                term = 21
            }
            return term
        }

        //	public String[] getYearTable() {
        //		setGregorian(gregorianYear, 1, 1);
        //		computeChineseFields();
        //		computeSolarTerms();
        //		String[] table = new String[58]; // 6*9 + 4
        //		table[0] = getTextLine(27, "公历年历：" + gregorianYear);
        //		table[1] = getTextLine(27, "农历年历：" + (chineseYear + 1) + " (" + KStemNames[(chineseYear + 1 - 1) % 10]
        //				+ KBranchNames[(chineseYear + 1 - 1) % 12] + " - " + animalNames[(chineseYear + 1 - 1) % 12] + "年)");
        //		int ln = 2;
        //		String blank = "                                         " + "  " + "                                         ";
        //		String[] mLeft = null;
        //		String[] mRight = null;
        //		for (int i = 1; i <= 6; i++) {
        //			table[ln] = blank;
        //			ln++;
        //			mLeft = getMonthTable();
        //			mRight = getMonthTable();
        //			for (int j = 0; j < mLeft.length; j++) {
        //				String line = mLeft[j] + "  " + mRight[j];
        //				table[ln] = line;
        //				ln++;
        //			}
        //		}
        //		table[ln] = blank;
        //		ln++;
        //		table[ln] = getTextLine(0, "##/## - 公历日期/农历日期，(*)#月 - (闰)农历月第一天");
        //		ln++;
        //		return table;
        //	}

        fun getTextLine(s: Int, t: String?): String {
            var str =
                "                                         " + "  " + "                                         "
            if (t != null && s < str.length && s + t.length < str.length) {
                str = str.substring(0, s) + t + str.substring(s + t.length)
            }
            return str
        }

        //	private static String[] monthNames = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };

        //	public String[] getMonthTable() {
        //		setGregorian(gregorianYear, gregorianMonth, 1);
        //		computeChineseFields();
        //		computeSolarTerms();
        //		String[] table = new String[8];
        //		String title = null;
        //		if (gregorianMonth < 11)
        //			title = "                   ";
        //		else
        //			title = "                 ";
        //		title = title + monthNames[gregorianMonth - 1] + "月" + "                   ";
        //		String header = "   日    一    二    三    四    五    六 ";
        //		String blank = "                                          ";
        //		table[0] = title;
        //		table[1] = header;
        //		int wk = 2;
        //		String line = "";
        //		for (int i = 1; i < dayOfWeek; i++) {
        //			line += "     " + ' ';
        //		}
        //		int days = daysInGregorianMonth(gregorianYear, gregorianMonth);
        //		for (int i = gregorianDate; i <= days; i++) {
        //			line += getDateString() + ' ';
        //			rollUpOneDay();
        //			if (dayOfWeek == 1) {
        //				table[wk] = line;
        //				line = "";
        //				wk++;
        //			}
        //		}
        //		for (int i = dayOfWeek; i <= 7; i++) {
        //			line += "     " + ' ';
        //		}
        //		table[wk] = line;
        //		for (int i = wk + 1; i < table.length; i++) {
        //			table[i] = blank;
        //		}
        //		for (int i = 0; i < table.length; i++) {
        //			table[i] = table[i].substring(0, table[i].length() - 1);
        //		}
        //
        //		return table;
        //	}

        private val chineseMonthNames =
            arrayOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊")
        private val KPrincipleTermNames =
            arrayOf("雨水", "春分", "谷雨", "小满", "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至", "大寒")
        private val KSectionalTermNames =
            arrayOf("立春", "惊蛰", "清明", "立夏", "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪", "小寒")
    }

    //	public int getDayOfYear() {
    //		return dayOfYear;
    //	}
    //
    //	public void setDayOfYear(int dayOfYear) {
    //		this.dayOfYear = dayOfYear;
    //	}
    //
    //	public int getDayOfWeek() {
    //		return dayOfWeek;
    //	}
    //
    //	public void setDayOfWeek(int dayOfWeek) {
    //		this.dayOfWeek = dayOfWeek;
    //	}
    //
    //	public int getChineseYear() {
    //		return mLunarYear;
    //	}
    //
    //	public void setChineseYear(int chineseYear) {
    //		this.mLunarYear = chineseYear;
    //	}
    //
    //	public int getChineseMonth() {
    //		return mLunarMonth;
    //	}
    //
    //	public void setChineseMonth(int chineseMonth) {
    //		this.mLunarMonth = chineseMonth;
    //	}
    //
    //	public int getChineseDate() {
    //		return mLunarDate;
    //	}
    //
    //	public void setChineseDate(int chineseDate) {
    //		this.mLunarDate = chineseDate;
    //	}
    //
    //	public int getSectionalTerm() {
    //		return sectionalTerm;
    //	}
    //
    //	public void setSectionalTerm(int sectionalTerm) {
    //		this.sectionalTerm = sectionalTerm;
    //	}
    //
    //	public int getPrincipleTerm() {
    //		return principleTerm;
    //	}
    //
    //	public void setPrincipleTerm(int principleTerm) {
    //		this.principleTerm = principleTerm;
    //	}
    //
    //	public static char[] getDaysInGregorianMonth() {
    //		return daysInGregorianMonth;
    //	}
    //
    //	public static void setDaysInGregorianMonth(char[] daysInGregorianMonth) {
    //		LunarUtil.daysInGregorianMonth = daysInGregorianMonth;
    //	}

    //	public static String[] getStemNames() {
    //		return KStemNames;
    //	}

    //	public static void setStemNames(String[] stemNames) {
    //		LunarUtil.stemNames = stemNames;
    //	}

    //	public static String[] getBranchNames() {
    //		return KBranchNames;
    //	}

    //	public static void setBranchNames(String[] branchNames) {
    //		LunarUtil.branchNames = branchNames;
    //	}

    //	public static String[] getAnimalNames() {
    //		return animalNames;
    //	}

    //	public static void setAnimalNames(String[] animalNames) {
    //		LunarUtil.animalNames = animalNames;
    //	}

    //	public static char[] getChineseMonths() {
    //		return chineseMonths;
    //	}
    //
    //	public static void setChineseMonths(char[] chineseMonths) {
    //		LunarUtil.chineseMonths = chineseMonths;
    //	}
    //
    //	public static int getBaseYear() {
    //		return baseYear;
    //	}
    //
    //	public static void setBaseYear(int baseYear) {
    //		LunarUtil.baseYear = baseYear;
    //	}
    //
    //	public static int getBaseMonth() {
    //		return baseMonth;
    //	}
    //
    //	public static void setBaseMonth(int baseMonth) {
    //		LunarUtil.baseMonth = baseMonth;
    //	}
    //
    //	public static int getBaseDate() {
    //		return baseDate;
    //	}
    //
    //	public static void setBaseDate(int baseDate) {
    //		LunarUtil.baseDate = baseDate;
    //	}
    //
    //	public static int getBaseIndex() {
    //		return baseIndex;
    //	}
    //
    //	public static void setBaseIndex(int baseIndex) {
    //		LunarUtil.baseIndex = baseIndex;
    //	}
    //
    //	public static int getBaseChineseYear() {
    //		return baseChineseYear;
    //	}
    //
    //	public static void setBaseChineseYear(int baseChineseYear) {
    //		LunarUtil.baseChineseYear = baseChineseYear;
    //	}
    //
    //	public static int getBaseChineseMonth() {
    //		return baseChineseMonth;
    //	}
    //
    //	public static void setBaseChineseMonth(int baseChineseMonth) {
    //		LunarUtil.baseChineseMonth = baseChineseMonth;
    //	}
    //
    //	public static int getBaseChineseDate() {
    //		return baseChineseDate;
    //	}
    //
    //	public static void setBaseChineseDate(int baseChineseDate) {
    //		LunarUtil.baseChineseDate = baseChineseDate;
    //	}
    //
    //	public static int[] getBigLeapMonthYears() {
    //		return bigLeapMonthYears;
    //	}
    //
    //	public static void setBigLeapMonthYears(int[] bigLeapMonthYears) {
    //		LunarUtil.bigLeapMonthYears = bigLeapMonthYears;
    //	}
    //
    //	public static char[][] getSectionalTermMap() {
    //		return sectionalTermMap;
    //	}
    //
    //	public static void setSectionalTermMap(char[][] sectionalTermMap) {
    //		LunarUtil.sectionalTermMap = sectionalTermMap;
    //	}
    //
    //	public static char[][] getSectionalTermYear() {
    //		return sectionalTermYear;
    //	}
    //
    //	public static void setSectionalTermYear(char[][] sectionalTermYear) {
    //		LunarUtil.sectionalTermYear = sectionalTermYear;
    //	}
    //
    //	public static char[][] getPrincipleTermMap() {
    //		return principleTermMap;
    //	}
    //
    //	public static void setPrincipleTermMap(char[][] principleTermMap) {
    //		LunarUtil.principleTermMap = principleTermMap;
    //	}
    //
    //	public static char[][] getPrincipleTermYear() {
    //		return principleTermYear;
    //	}
    //
    //	public static void setPrincipleTermYear(char[][] principleTermYear) {
    //		LunarUtil.principleTermYear = principleTermYear;
    //	}
    //
    //	public static String[] getMonthNames() {
    //		return monthNames;
    //	}
    //
    //	public static void setMonthNames(String[] monthNames) {
    //		LunarUtil.monthNames = monthNames;
    //	}
    //
    //	public static String[] getChineseMonthNames() {
    //		return chineseMonthNames;
    //	}
    //
    //	public static void setChineseMonthNames(String[] chineseMonthNames) {
    //		LunarUtil.chineseMonthNames = chineseMonthNames;
    //	}
    //
    //	public static String[] getPrincipleTermNames() {
    //		return principleTermNames;
    //	}
    //
    //	public static void setPrincipleTermNames(String[] principleTermNames) {
    //		LunarUtil.principleTermNames = principleTermNames;
    //	}
    //
    //	public static String[] getSectionalTermNames() {
    //		return sectionalTermNames;
    //	}
    //
    //	public static void setSectionalTermNames(String[] sectionalTermNames) {
    //		LunarUtil.sectionalTermNames = sectionalTermNames;
    //	}

    //	public static void main(String[] arg) {
    //		LunarUtil2 c = new LunarUtil2();
    //		String cmd = "day";
    //		int y = 2010;
    //		int m = 8;
    //		int d = 11;
    //
    //		c.setGregorian(y, m, d);
    //		c.computeChineseFields();
    //		c.computeSolarTerms();
    //
    //		if (cmd.equalsIgnoreCase("year")) {
    //			String[] t = c.getYearTable();
    //			for (int i = 0; i < t.length; i++)
    //				System.out.println(t[i]);
    //		} else if (cmd.equalsIgnoreCase("month")) {
    //			String[] t = c.getMonthTable();
    //			for (int i = 0; i < t.length; i++)
    //				System.out.println(t[i]);
    //		} else {
    //			System.out.println(c.toString());
    //		}
    //		System.out.println(c.getDateString());
    //
    //	}

}