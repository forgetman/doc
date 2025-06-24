package xy.calendar.design.model.skin

import android.graphics.Color
import lib.base.model.Elem
import java.util.*

enum class ClockType {
    ENumeric,
    EAnalog
}

enum class HolidayAlign(val desc: String) {
    EAuto("EAuto"), // 自动对齐
    ECloseByDate("ECloseByDate") // 紧贴日期
}

/**
 * @author yuansui
 */
abstract class BaseSkin {

    private val map: MutableMap<String, Elem> = HashMap()

    fun getColor(color: String): Int {
        val tmpColor = Integer.parseInt(color, 16)
        return Color.rgb(Color.red(tmpColor), Color.green(tmpColor), Color.blue(tmpColor))
    }
}

class YearSkin : BaseSkin() {
}

/**
 * 时钟皮肤元素
 */
class ClockSkin() : BaseSkin() {
    var clockType: ClockType? = null
    var hasClock: Boolean? = null
}

class MonthSkin : BaseSkin() {
}

class DaySkin : BaseSkin() {
}