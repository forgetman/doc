package lib.base.model

import java.util.*

/**
 * @author yuansui
 */
class Date private constructor(val calendar: Calendar) {
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH)
    var week = calendar.get(Calendar.DAY_OF_WEEK)
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var hour = calendar.get(Calendar.HOUR_OF_DAY)
    var minute = calendar.get(Calendar.MINUTE)

    companion object {
        fun new(): Date = Date(Calendar.getInstance())
    }

    fun offsetMonth(month: Int): Date = Calendar.getInstance().let {
        it.time = calendar.time
        it.add(Calendar.MONTH, month)
        Date(it)
    }

}
