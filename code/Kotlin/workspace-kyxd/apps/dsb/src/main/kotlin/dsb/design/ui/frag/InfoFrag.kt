package dsb.design.ui.frag

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import dsb.R
import dsb.databinding.FragInfoBinding
import dsb.design.viewModel.InfoViewModel
import dsb.ext.withToast
import dsb.ext.withViewState
import lib.base.design.frag.BaseDBFrag
import vector.app.adapter.pager.viewpager.FragStatePagerAdapter
import vector.app.databinding.frag.DBFragEx
import vector.bindingadapter.TabLayoutAttrs
import vector.os.colorRes

/**
 * 资讯
 * @author yuansui
 * @since 2019/1/17
 */
@AndroidEntryPoint
class InfoFrag : BaseDBFrag<InfoViewModel>() {

    val adapter by lazy(LazyThreadSafetyMode.NONE) {
        FragStatePagerAdapter(this)
    }

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.IDLE

    val tabLayoutAttr = TabLayoutAttrs.build {
        gravity = TabLayoutAttrs.Gravity.CENTER
        setIndicatorColor(Color.WHITE)
        mode = TabLayoutAttrs.Mode.SCROLLABLE
        textColorNormal = R.color.text_df.colorRes
        textColorSelected = R.color.white.colorRes
        listener = object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var view = tab?.customView
                if (view == null) {
                    tab?.setCustomView(R.layout.layout_tab_layout_item)
                    view = tab?.customView
                }

                val tv = view?.findViewById<TextView>(android.R.id.text1)
                tv?.setTextColor(tab?.parent?.tabTextColors)
                tv?.typeface = Typeface.DEFAULT
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var view = tab?.customView
                if (null == view) {
                    tab?.setCustomView(R.layout.layout_tab_layout_item)
                    view = tab?.customView
                }

                val tv = view?.findViewById<TextView>(android.R.id.text1)
                tv?.setTextColor(tab?.parent?.tabTextColors)
                tv?.typeface = Typeface.DEFAULT_BOLD
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragInfoBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.onlyFlatBar()
    }

    override fun flowOfSetup() {
        fetchCategory()
    }

    override fun onRetryClick() {
        fetchCategory()
    }

    private fun fetchCategory() {
        viewModel.fetchCategory().withViewState(this).withToast()
    }
}
