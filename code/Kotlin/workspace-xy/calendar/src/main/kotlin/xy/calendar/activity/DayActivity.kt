package xy.calendar.activity

import android.view.View
import xy.calendar.activity.base.base.BaseLoopVPActivity
import xy.calendar.design.viewModel.DayLoopViewModel

/**
 * @author yuansui
 * @since 2018/5/17
 */
class DayActivity : BaseLoopVPActivity<DayLoopViewModel>() {


    override fun createContentView(): View {
        TODO("Not yet implemented")
    }

    override fun onDetectorScale(scaleFactor: Float): Boolean = false

    override fun onPageSelected(position: Int, prePosition: Int) {
        // 下标假设为0, 1, 2, 初始化为1
        if (position < prePosition) {
            // 右滑, 更改2的数据为0之前的日期
//            getItem(prePosition + 1).offsetDay(-3)
        } else {
            // 左滑, 更改0的数据为2之后的日期
//            getItem(prePosition - 1).offsetDay(3)
        }
    }

}