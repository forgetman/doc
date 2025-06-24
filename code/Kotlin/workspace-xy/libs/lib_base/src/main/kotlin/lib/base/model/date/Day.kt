package lib.base.model.date


import android.graphics.RectF
import lib.base.Constants
import lib.base.model.Date
import lib.base.util.LunarUtil
import lib.base.util.festival.FestivalUtil
import vector.app.util.Res
import java.util.*

enum class DescType {
    LUNAR, // 农历
    SOLAR_TERM, // 节气
    FESTIVAL // 节日
}

enum class DayType {
    NORMAL,
    WEEKEND,
    NOT_CUR,
    TODAY,
}

class Day(private val cal: Calendar, private val type: DayType) {
    private val lunarUtil = LunarUtil(cal) // 农历详细
    private val rect = RectF()

    var week: String
    var week_num: String
    var year: Int
    var month: Int
    var day: Int
    var desc: String? // 农历或节日信息
    var desc_type: DescType // 描述类型

    var date_desc: String // 完整的日期描述
    var lunar: String // 农历

    //
    var solar_term: String // 节气
    var next_solar_term: String // 下一个节气
    var next_solar_term_day_diff: Int // 距离下一个节气的天数


    init {
        val resId = Res.getIdentifier(
            Constants.KWeekPrefix + this.cal.get(Calendar.DAY_OF_WEEK),
            Res.Type.STRING
        )
        week = Constants.KWeekDayDefPrefix + Res.getString(resId)

        week_num = Constants.KOrderPrefix + cal.get(Calendar.WEEK_OF_YEAR) + Constants.KWeekDefStr

        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DATE)

        date_desc = createDateDesc()

        lunar = String()
            .plus(Constants.KLunarStr)
            .plus(" ")
            .plus(lunarUtil.lunarPrefix)
            .plus(lunarUtil.lunarMonthStr)
            .plus(lunarUtil.lunarDayStr)

        solar_term = lunarUtil.solarTerm
        next_solar_term = lunarUtil.getNextSolarTermDesc(cal.time)
        next_solar_term_day_diff = lunarUtil.getNextSolarTermDayDiff(cal.time)

        /**
         * 获取描述
         */
        // 先获取节气
        desc = solar_term
        desc_type = DescType.SOLAR_TERM

        if (desc.isNullOrEmpty()) {
            // 如果没有节气
            if (type != DayType.NOT_CUR) {
                // 只有当月才添加节日
                desc_type = DescType.FESTIVAL
                desc = FestivalUtil.getName(cal)
            } else {
                // 显示农历
                desc_type = DescType.LUNAR
                desc = lunarUtil.lunarDayStr
                if (desc == "初一") {
                    desc = lunarUtil.lunarMonthStr
                }
            }
        }
    }

    fun setPointRect(left: Float, top: Float, right: Float, bottom: Float) {
        rect.set(left, top, right, bottom)
    }

    fun offsetDay(day: Int): Day {
        val c = Calendar.getInstance()
        c.time = cal.time
        c.add(Calendar.DATE, day)

        return Day(c, type)
    }

    private fun createDateDesc(): String {
        return String()
            .plus(cal.get(Calendar.YEAR))
            .plus(Constants.KYearDefStr)
            .plus(cal.get(Calendar.MONTH) + 1)
            .plus(Constants.KMonthDefStr)
    }

    fun isToday(date: Date): Boolean {
        return date.year == year && date.month == month && date.day == day
    }
}