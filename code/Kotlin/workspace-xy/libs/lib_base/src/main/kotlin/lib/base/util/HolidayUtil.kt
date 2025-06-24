package lib.base.util

import android.annotation.SuppressLint
import lib.base.R
import vector.app.util.Res
import java.io.IOException
import java.util.*


@SuppressLint("UseSparseArrays")
object HolidayUtil {
    private const val MONTH_DEFINE = "m"

    private val monthMap = HashMap<Int, Holiday>()/*month*/
    private var templateContent: String? = null

    enum class HolidayType {
        REST,
        WORK
    }

    init {
        readTemplateContent()
    }

    /**
     * 读取模板
     *
     * @throws IOException
     */
    private fun readTemplateContent() {
        templateContent = Res.getRaw(R.raw.holiday).toString()
        generateHoliday()
    }

    private fun generateHoliday() {
        val tokenizer = StringTokenizer(templateContent, "\r\n")

        var wIndex = 0
        var hIndex = 0
        var month = 0
        var day = 0

        var index = 0
        var index2 = 0

        while (tokenizer.hasMoreTokens()) {
            val line = tokenizer.nextToken()
            wIndex = line.indexOf("W")
            hIndex = line.indexOf("H")

            if (wIndex == 0) {
                // 工作日期 W(m1:d6)
                index = line.indexOf(MONTH_DEFINE) // 2
                index2 = line.indexOf(":") // 4
                month = Integer.valueOf(line.substring(index + 1, index2))
                day = Integer.valueOf(line.substring(index2 + 2, line.length - 1))
                var holiday: Holiday? = monthMap[month]
                if (holiday == null) {
                    holiday = Holiday()
                    monthMap[month] = holiday
                }

                holiday.dayMap[day] = HolidayType.WORK

            } else if (hIndex == 0) {
                // 放假日期 H(m1:d1)
                index = line.indexOf(MONTH_DEFINE) // 2
                index2 = line.indexOf(":") // 4
                month = Integer.valueOf(line.substring(index + 1, index2))
                day = Integer.valueOf(line.substring(index2 + 2, line.length - 1))

                var holiday: Holiday? = monthMap[month]
                if (holiday == null) {
                    holiday = Holiday()
                    monthMap[month] = holiday
                }
                holiday.dayMap[day] = HolidayType.REST
            }
        }
    }

    fun getHoliday(month: Int, day: Int): HolidayType? {
        return monthMap[month + 1]?.dayMap?.get(day)
    }

    private class Holiday {
        val dayMap = HashMap<Int, HolidayType>()
    }
}
