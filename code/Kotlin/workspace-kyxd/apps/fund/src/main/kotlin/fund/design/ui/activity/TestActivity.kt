package fund.design.ui.activity

import androidx.databinding.ViewDataBinding
import fund.R
import fund.databinding.ActivitiyTestBinding
import fund.design.viewModel.TestViewModel
import lib.base.design.adapter.TestAdapter
import vector.design.ui.activity.ActivityEx
import vector.fitter.Fitter

/**
 * @author yuansui
 * @since 2018/8/3
 */
class TestActivity : ActivityEx<TestViewModel>() {

    val adapter = TestAdapter()

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val b = ActivitiyTestBinding.inflate(layoutInflater)
        b.owner = this
        b.viewModel = viewModel
        return b
    }

    override fun flowOfNavBar() {
        navBar.mid.addText {
            text = "测试页"
            colorId = R.color.text_222
            textSize = DpFitter.get().dp(16)
        }
    }
}