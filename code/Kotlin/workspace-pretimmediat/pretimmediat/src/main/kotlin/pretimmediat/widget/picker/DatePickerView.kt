package pretimmediat.widget.picker

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import pretimmediat.R
import pretimmediat.ext.formatDate
import java.text.DecimalFormat
import java.util.Calendar

/**
 * FIXME: 随便找的网上代码
 *
 * 通过时间戳初始换时间选择器，毫秒级别
 *
 * @param context        Activity Context
 * @param callback       选择结果回调
 * @param beginTimestamp 毫秒级时间戳
 * @param endTimestamp   毫秒级时间戳
 */
class DatePickerView(
    private val context: Context,
    beginTimestamp: Long,
    endTimestamp: Long,
    private val callback: Callback
) {

    private val mBeginTime: Calendar
    private val mEndTime: Calendar
    private val mSelectedTime: Calendar
    private var mCanDialogShow: Boolean

    private var mPickerDialog: Dialog? = null
    private var mDpvYear: PickerView? = null
    private var mDpvMonth: PickerView? = null
    private var mDpvDay: PickerView? = null

    private var mBeginYear = 0
    private var mBeginMonth = 0
    private var mBeginDay = 0
    private var mBeginHour = 0
    private var mBeginMinute = 0
    private var mEndYear = 0
    private var mEndMonth = 0
    private var mEndDay = 0
    private var mEndHour = 0
    private var mEndMinute = 0
    private val mYearUnits: MutableList<String> = ArrayList()
    private val mMonthUnits: MutableList<String> = ArrayList()
    private val mDayUnits: MutableList<String> = ArrayList()
    private val mDecimalFormat = DecimalFormat("00")

    private var mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE

    /**
     * 时间选择结果回调接口
     */
    fun interface Callback {
        fun onTimeSelected(timestamp: Long)
    }

    init {
        if (beginTimestamp >= endTimestamp) {
            mCanDialogShow = false
            throw IllegalStateException("CustomDatePicker init error.")
        }

        mBeginTime = Calendar.getInstance()
        mBeginTime.timeInMillis = beginTimestamp
        mEndTime = Calendar.getInstance()
        mEndTime.timeInMillis = endTimestamp
        mSelectedTime = Calendar.getInstance()

        initView()
        initData()
        mCanDialogShow = true
    }

    private fun initView() {
        mPickerDialog = Dialog(context, R.style.Theme_Dialog_DimEnable_DatePicker)
        mPickerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mPickerDialog!!.setContentView(R.layout.dialog_date_picker)

        val window = mPickerDialog!!.window
        if (window != null) {
            val lp = window.attributes
            lp.gravity = Gravity.BOTTOM
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
        }

        mPickerDialog!!.findViewById<View>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        mPickerDialog!!.findViewById<View>(R.id.tv_confirm).setOnClickListener {
            callback.onTimeSelected(mSelectedTime.timeInMillis)
            dismiss()
        }

        mDpvYear = mPickerDialog!!.findViewById<PickerView?>(R.id.dpv_year).apply {
            setOnSelectListener { _, _, selected ->
                val timeUnit: Int = selected.toIntOrNull() ?: return@setOnSelectListener
                mSelectedTime.set(Calendar.YEAR, timeUnit)
                linkageMonthUnit(true, LINKAGE_DELAY_DEFAULT)
            }
        }
        mDpvMonth = mPickerDialog!!.findViewById<PickerView?>(R.id.dpv_month).apply {
            setOnSelectListener { _, _, selected ->
                val timeUnit: Int = selected.toIntOrNull() ?: return@setOnSelectListener
                val lastSelectedMonth = mSelectedTime.get(Calendar.MONTH) + 1
                mSelectedTime.add(Calendar.MONTH, timeUnit - lastSelectedMonth);
                linkageDayUnit(true, LINKAGE_DELAY_DEFAULT)
            }
        }
        mDpvDay = mPickerDialog!!.findViewById<PickerView?>(R.id.dpv_day).apply {
            setOnSelectListener { _, _, selected ->
                val timeUnit: Int = selected.toIntOrNull() ?: return@setOnSelectListener
                mSelectedTime.set(Calendar.DAY_OF_MONTH, timeUnit);
                linkageHourUnit(true, LINKAGE_DELAY_DEFAULT);
            }
        }
    }

    private fun dismiss() {
        if (mPickerDialog != null && mPickerDialog!!.isShowing) {
            mPickerDialog!!.dismiss()

            mDpvYear!!.onDestroy()
            mDpvMonth!!.onDestroy()
            mDpvDay!!.onDestroy()
        }
    }

    private fun initData() {
        mSelectedTime.timeInMillis = mBeginTime.timeInMillis

        mBeginYear = mBeginTime[Calendar.YEAR]
        // Calendar.MONTH 值为 0-11
        mBeginMonth = mBeginTime[Calendar.MONTH] + 1
        mBeginDay = mBeginTime[Calendar.DAY_OF_MONTH]
        mBeginHour = mBeginTime[Calendar.HOUR_OF_DAY]
        mBeginMinute = mBeginTime[Calendar.MINUTE]

        mEndYear = mEndTime[Calendar.YEAR]
        mEndMonth = mEndTime[Calendar.MONTH] + 1
        mEndDay = mEndTime[Calendar.DAY_OF_MONTH]
        mEndHour = mEndTime[Calendar.HOUR_OF_DAY]
        mEndMinute = mEndTime[Calendar.MINUTE]

        val canSpanYear = mBeginYear != mEndYear
        val canSpanMon = !canSpanYear && mBeginMonth != mEndMonth
        val canSpanDay = !canSpanMon && mBeginDay != mEndDay
        val canSpanHour = !canSpanDay && mBeginHour != mEndHour
        val canSpanMinute = !canSpanHour && mBeginMinute != mEndMinute
        if (canSpanYear) {
            initDateUnits(
                MAX_MONTH_UNIT,
                mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH),
                MAX_HOUR_UNIT,
                MAX_MINUTE_UNIT
            )
        } else if (canSpanMon) {
            initDateUnits(
                mEndMonth,
                mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH),
                MAX_HOUR_UNIT,
                MAX_MINUTE_UNIT
            )
        } else if (canSpanDay) {
            initDateUnits(mEndMonth, mEndDay, MAX_HOUR_UNIT, MAX_MINUTE_UNIT)
        } else if (canSpanHour) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, MAX_MINUTE_UNIT)
        } else if (canSpanMinute) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, mEndMinute)
        }
    }

    private fun initDateUnits(endMonth: Int, endDay: Int, endHour: Int, endMinute: Int) {
        for (i in mBeginYear..mEndYear) {
            mYearUnits.add(i.toString())
        }

        for (i in mBeginMonth..endMonth) {
            mMonthUnits.add(mDecimalFormat.format(i.toLong()))
        }

        for (i in mBeginDay..endDay) {
            mDayUnits.add(mDecimalFormat.format(i.toLong()))
        }

        mDpvYear!!.setDataList(mYearUnits)
        mDpvYear!!.setSelected(0)
        mDpvMonth!!.setDataList(mMonthUnits)
        mDpvMonth!!.setSelected(0)
        mDpvDay!!.setDataList(mDayUnits)
        mDpvDay!!.setSelected(0)

        setCanScroll()
    }

    private fun setCanScroll() {
        mDpvYear!!.setCanScroll(mYearUnits.size > 1)
        mDpvMonth!!.setCanScroll(mMonthUnits.size > 1)
        mDpvDay!!.setCanScroll(mDayUnits.size > 1)
    }

    /**
     * 联动“月”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private fun linkageMonthUnit(showAnim: Boolean, delay: Long) {
        val minMonth: Int
        val maxMonth: Int
        val selectedYear = mSelectedTime[Calendar.YEAR]
        if (mBeginYear == mEndYear) {
            minMonth = mBeginMonth
            maxMonth = mEndMonth
        } else if (selectedYear == mBeginYear) {
            minMonth = mBeginMonth
            maxMonth = MAX_MONTH_UNIT
        } else if (selectedYear == mEndYear) {
            minMonth = 1
            maxMonth = mEndMonth
        } else {
            minMonth = 1
            maxMonth = MAX_MONTH_UNIT
        }

        // 重新初始化时间单元容器
        mMonthUnits.clear()
        for (i in minMonth..maxMonth) {
            mMonthUnits.add(mDecimalFormat.format(i.toLong()))
        }
        mDpvMonth!!.setDataList(mMonthUnits)

        // 确保联动时不会溢出或改变关联选中值
        val selectedMonth = getValueInRange(mSelectedTime[Calendar.MONTH] + 1, minMonth, maxMonth)
        mSelectedTime[Calendar.MONTH] = selectedMonth - 1
        mDpvMonth!!.setSelected(selectedMonth - minMonth)
        if (showAnim) {
            mDpvMonth!!.startAnim()
        }

        // 联动“日”变化
        mDpvMonth!!.postDelayed({ linkageDayUnit(showAnim, delay) }, delay)
    }

    /**
     * 联动“日”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private fun linkageDayUnit(showAnim: Boolean, delay: Long) {
        val minDay: Int
        val maxDay: Int
        val selectedYear = mSelectedTime[Calendar.YEAR]
        val selectedMonth = mSelectedTime[Calendar.MONTH] + 1
        if (mBeginYear == mEndYear && mBeginMonth == mEndMonth) {
            minDay = mBeginDay
            maxDay = mEndDay
        } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth) {
            minDay = mBeginDay
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH)
        } else if (selectedYear == mEndYear && selectedMonth == mEndMonth) {
            minDay = 1
            maxDay = mEndDay
        } else {
            minDay = 1
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        mDayUnits.clear()
        for (i in minDay..maxDay) {
            mDayUnits.add(mDecimalFormat.format(i.toLong()))
        }
        mDpvDay!!.setDataList(mDayUnits)

        val selectedDay = getValueInRange(mSelectedTime[Calendar.DAY_OF_MONTH], minDay, maxDay)
        mSelectedTime[Calendar.DAY_OF_MONTH] = selectedDay
        mDpvDay!!.setSelected(selectedDay - minDay)
        if (showAnim) {
            mDpvDay!!.startAnim()
        }

        mDpvDay!!.postDelayed({ linkageHourUnit(showAnim, delay) }, delay)
    }

    /**
     * 联动“时”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private fun linkageHourUnit(showAnim: Boolean, delay: Long) {
        if ((mScrollUnits and SCROLL_UNIT_HOUR) == SCROLL_UNIT_HOUR) {
            val minHour: Int
            val maxHour: Int
            val selectedYear = mSelectedTime[Calendar.YEAR]
            val selectedMonth = mSelectedTime[Calendar.MONTH] + 1
            val selectedDay = mSelectedTime[Calendar.DAY_OF_MONTH]
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay) {
                minHour = mBeginHour
                maxHour = mEndHour
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay) {
                minHour = mBeginHour
                maxHour = MAX_HOUR_UNIT
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay) {
                minHour = 0
                maxHour = mEndHour
            } else {
                minHour = 0
                maxHour = MAX_HOUR_UNIT
            }

            val selectedHour =
                getValueInRange(mSelectedTime[Calendar.HOUR_OF_DAY], minHour, maxHour)
            mSelectedTime[Calendar.HOUR_OF_DAY] = selectedHour
        }
    }

    /**
     * 联动“分”变化
     *
     * @param showAnim 是否展示滚动动画
     */
    private fun linkageMinuteUnit(showAnim: Boolean) {
        if ((mScrollUnits and SCROLL_UNIT_MINUTE) == SCROLL_UNIT_MINUTE) {
            val minMinute: Int
            val maxMinute: Int
            val selectedYear = mSelectedTime[Calendar.YEAR]
            val selectedMonth = mSelectedTime[Calendar.MONTH] + 1
            val selectedDay = mSelectedTime[Calendar.DAY_OF_MONTH]
            val selectedHour = mSelectedTime[Calendar.HOUR_OF_DAY]
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay && mBeginHour == mEndHour) {
                minMinute = mBeginMinute
                maxMinute = mEndMinute
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay && selectedHour == mBeginHour) {
                minMinute = mBeginMinute
                maxMinute = MAX_MINUTE_UNIT
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay && selectedHour == mEndHour) {
                minMinute = 0
                maxMinute = mEndMinute
            } else {
                minMinute = 0
                maxMinute = MAX_MINUTE_UNIT
            }

            val selectedMinute =
                getValueInRange(mSelectedTime[Calendar.MINUTE], minMinute, maxMinute)
            mSelectedTime[Calendar.MINUTE] = selectedMinute
        }

        setCanScroll()
    }

    private fun getValueInRange(value: Int, minValue: Int, maxValue: Int): Int {
        return if (value < minValue) {
            minValue
        } else if (value > maxValue) {
            maxValue
        } else {
            value
        }
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    fun show(dateStr: String?) {
        if (!canShow() || dateStr.isNullOrEmpty()) {
            return
        }

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, false)) {
            mPickerDialog!!.show()
        }
    }

    private fun canShow(): Boolean {
        return mCanDialogShow && mPickerDialog != null
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    fun setSelectedTime(dateStr: String?, showAnim: Boolean): Boolean {
        return (canShow() && !dateStr.isNullOrEmpty() && setSelectedTime(
            dateStr.formatDate(),
            showAnim
        ))
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    fun show(timestamp: Long) {
        if (!canShow()) {
            return
        }

        if (setSelectedTime(timestamp, false)) {
            mPickerDialog!!.show()
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @return 是否设置成功
     */
    fun setSelectedTime(timestamp: Long, showAnim: Boolean): Boolean {
        var timestamp = timestamp
        if (!canShow()) {
            return false
        }

        if (timestamp < mBeginTime.timeInMillis) {
            timestamp = mBeginTime.timeInMillis
        } else if (timestamp > mEndTime.timeInMillis) {
            timestamp = mEndTime.timeInMillis
        }
        mSelectedTime.timeInMillis = timestamp

        mYearUnits.clear()
        for (i in mBeginYear..mEndYear) {
            mYearUnits.add(i.toString())
        }
        mDpvYear!!.setDataList(mYearUnits)
        mDpvYear!!.setSelected(mSelectedTime[Calendar.YEAR] - mBeginYear)
        linkageMonthUnit(showAnim, if (showAnim) LINKAGE_DELAY_DEFAULT else 0)
        return true
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    fun setCancelable(cancelable: Boolean) {
        if (!canShow()) {
            return
        }

        mPickerDialog!!.setCancelable(cancelable)
    }

    private fun initScrollUnit(vararg units: Int) {
        if (units.isEmpty()) {
            mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE
        } else {
            for (unit in units) {
                mScrollUnits = mScrollUnits xor unit
            }
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    fun setScrollLoop(canLoop: Boolean) {
        if (!canShow()) {
            return
        }

        mDpvYear!!.setCanScrollLoop(canLoop)
        mDpvMonth!!.setCanScrollLoop(canLoop)
        mDpvDay!!.setCanScrollLoop(canLoop)
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    fun setCanShowAnim(canShowAnim: Boolean) {
        if (!canShow()) {
            return
        }

        mDpvYear!!.setCanShowAnim(canShowAnim)
        mDpvMonth!!.setCanShowAnim(canShowAnim)
        mDpvDay!!.setCanShowAnim(canShowAnim)
    }

    /**
     * 销毁弹窗
     */
    fun onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog!!.dismiss()
            mPickerDialog = null

            mDpvYear!!.onDestroy()
            mDpvMonth!!.onDestroy()
            mDpvDay!!.onDestroy()
        }
    }

    companion object {
        /**
         * 时间单位：时、分
         */
        private const val SCROLL_UNIT_HOUR = 1
        private const val SCROLL_UNIT_MINUTE = 2

        /**
         * 时间单位的最大显示值
         */
        private const val MAX_MINUTE_UNIT = 59
        private const val MAX_HOUR_UNIT = 23
        private const val MAX_MONTH_UNIT = 12

        /**
         * 级联滚动延迟时间
         */
        private const val LINKAGE_DELAY_DEFAULT = 100L
    }
}