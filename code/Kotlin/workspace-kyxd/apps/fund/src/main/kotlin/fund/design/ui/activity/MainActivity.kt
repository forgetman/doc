package fund.design.ui.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import fund.Bus
import fund.R
import fund.databinding.ActivityMainBinding
import fund.design.ui.frag.HomeFrag
import fund.design.ui.frag.MeFrag
import fund.design.viewModel.MainViewModel
import live.LiveInt
import vector.bindingadapter.onBind.Bind
import vector.design.ui.activity.ActivityEx
import vector.design.ui.adapter.FragPagerAdapter

/**
 * @author yuansui
 * @since 2018/7/19
 */
class MainActivity : ActivityEx<MainViewModel>() {

    private object MainPage {
        const val HOME = 0
        const val MSG = 1
        const val ME = 2
    }

    val adapter = FragPagerAdapter(supportFragmentManager)
    val data = mutableListOf<Fragment>().apply {
        add(HomeFrag())
//        add(MsgFrag())
        add(MeFrag())
    }
    val currPosition = Live<Int>()

    private var preTab: View? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    val onHomeClick = Bind.OnClick {
        setTab(it)
        switchTag(MainPage.HOME)
    }

    val onMsgClick = Bind.OnClick {
        setTab(it)
        // FIXME: LOGIN
        switchTag(MainPage.MSG)
    }

    val onMeClick = Bind.OnClick {
        setTab(it)
        switchTag(MainPage.ME)
    }

    val onPageSelected = Bind.Pager.onPageSelected {
        preTab?.isSelected = false
//        preTab = layoutTabs[it]
        preTab?.isSelected = true
    }

    private fun switchTag(position: Int) {
        currPosition.value = position
    }

    private fun setTab(v: View) {
        v.isSelected = true
        preTab?.isSelected = false
        preTab = v
    }

    override fun onDestroy() {
        super.onDestroy()

        Bus.close()
    }
}