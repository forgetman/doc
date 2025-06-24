package reader.pattern.frag.main

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import reader.R
import reader.databinding.FragBookCityBinding
import reader.network.api.Category
import reader.pattern.activity.SearchActivity
import reader.pattern.frag.leaderboard.LeaderboardFragCreator
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.build
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.startActivity
import vector.app.frag.LazyLoadMode
import vector.app.os.colorRes
import vector.app.os.drawableRes
import vector.app.util.toColor
import vector.app.util.toColorStateList
import vector.app.util.toDrawable
import vector.bindingadapter.TabLayoutAttrs
import vector.widget.viewpager2.adapter.FragStatePagerAdapter2

/**
 * @author yuansui
 * @since 2019-06-12
 */
@LayoutBindingClass<FragBookCityBinding>
class BookCityFrag : SimpleDBFragEx() {

    val adapter by lazy(LazyThreadSafetyMode.NONE) {
        FragStatePagerAdapter2(this)
    }

    val pager = FragPager.build(Category.entries.size, Category.entries.map { it.desc }) {
        val type = Category.entries[it]
        LeaderboardFragCreator.create(type).get()
    }

    val tabLayoutAttr by lazy(LazyThreadSafetyMode.NONE) {
        TabLayoutAttrs.build {
            mode = TabLayoutAttrs.Mode.FIXED
            gravity = TabLayoutAttrs.Gravity.FILL

            setIndicatorDrawable(
                R.drawable.layer_book_city_indicator.toDrawable(context),
                R.color.blue.toColor(context)
            )
            indicatorFullWidth = false

            tabRippleColor = R.color.highlight_primary.toColorStateList(context)

            textColorNormal = R.color.text_primary.colorRes
            textColorSelected = R.color.text_primary.colorRes
        }
    }

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.IDLE

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragBookCityBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText {
            text = "书城"
            bold = true
        }

        appBar.left.addIcon(R.drawable.nav_bar_ic_search.drawableRes) {
            startActivity<SearchActivity>()
        }
    }
}