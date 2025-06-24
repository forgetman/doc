package vector.widget.databinding.indicator.adapter

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import vector.app.os.dp
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.indicator.page.IconPageIndicator
import vector.widget.indicator.page.PageIndicator
import vector.widget.indicator.page.setupWithViewPager
import vector.widget.viewpager.ViewPager

object ViewPagerBinding {

    private const val INDICATOR = BINDING_PREFIX + "viewPager_indicator"
    private const val INDICATOR_ICON_CONFIG = BINDING_PREFIX + "viewPager_indicator_config_icon"

    @JvmStatic
    @BindingAdapter(INDICATOR)
    fun <T : PageIndicator> setIndicator(view: ViewPager, indicator: T) {
        indicator.setupWithViewPager(view)
    }

    @JvmStatic
    @BindingAdapter(INDICATOR, INDICATOR_ICON_CONFIG, requireAll = false)
    fun setIndicator(view: ViewPager, @IdRes id: Int, config: IconPageIndicator.Config?) {
        val parent = view.rootView
        val v = parent?.findViewById<View>(id)
        if (v is PageIndicator) {
            if (v is IconPageIndicator && config != null) {
                val space = config.space.dp.toPx(view.context).toInt()
                v.setIndicatorSpace(space)
                val size = config.size.dp.toPx(view.context)
                v.setIndicatorSize(size)
                v.setResId(config.resId)
            }

            v.setupWithViewPager(view)
        }
    }
}