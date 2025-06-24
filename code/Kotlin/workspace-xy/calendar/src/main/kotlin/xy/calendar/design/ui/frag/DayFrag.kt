package xy.calendar.design.ui.frag

import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import lib.base.model.date.Day
import vector.app.databinding.frag.DBFragEx
import vector.app.ext.bind.bindView
import vector.widget.ImageView
import xy.calendar.R
import xy.calendar.design.model.skin.SkinMgr
import xy.calendar.design.viewModel.DayViewModel
import xy.calendar.maker.DayMaker

/**
 * @author yuansui
 * @since 2018/5/17
 */
class DayFrag : DBFragEx<DayViewModel>() {

    companion object {
        private const val KCnTextDis = "距"
        private const val KTodaySolarTerm = "今日节气"
        private const val KTodayFestival = "今日节日"
        private const val KRemainStr = "还有"
        private const val KDayStr = "天"
        private const val KSpace = " "

        private const val KFestivalSplit = ";"

        private const val KFcFestivalDay = 60
        private const val KFcFestivalDayNext = 5
    }

    private var day: Day? = null
    private val skin = SkinMgr.day

    private val mIvFg by bindView<ImageView>(R.id.day_iv_fg)
    private val mTvDayNum by bindView<TextView>(R.id.day_tv_day_number)
    private val mTvDateDesc by bindView<TextView>(R.id.day_tv_date_desc)
    private val mTvWeekNum by bindView<TextView>(R.id.day_tv_week_num)
    private val mTvWeek by bindView<TextView>(R.id.day_tv_week)
    private val mTvLunar by bindView<TextView>(R.id.day_tv_lunar)
    private val mTvSolarTerm by bindView<TextView>(R.id.day_tv_solar_term)
    private val mTvFestival by bindView<TextView>(R.id.day_tv_solar_term)
    private val mTvFestivalNext by bindView<TextView>(R.id.day_tv_festival_next)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initializeContentView() {
//        mIvFg.url(DayMaker.fg)
    }

    private fun initTv(tv: TextView, key: String) {
//        val elem = skin.getElem(key)
//        tv.setTextColor(mDaySkin.getColor(TDaySkin.TextColor))
//        ViewSetter.textSize(tv, elem.getFloat(Elem.size))
    }
}