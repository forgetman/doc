package vector.widget.indicator.page

import android.database.DataSetObserver
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener
import vector.widget.indicator.R
import vector.widget.viewpager.ViewPager

internal class PageIndicatorMediator(private val indicator: PageIndicator, viewPager: ViewPager) {

    init {
        val adapter = viewPager.adapter
        if (adapter == null) {
            viewPager.addOnAdapterChangeListener(object : OnAdapterChangeListener {

                override fun onAdapterChanged(
                    viewPager: androidx.viewpager.widget.ViewPager,
                    oldAdapter: PagerAdapter?,
                    newAdapter: PagerAdapter?
                ) {
                    if (newAdapter == null) return // 暂时不考虑adapter会被remove的事情(indicator没支持)
                    viewPager.removeOnAdapterChangeListener(this)

                    indicator.setViewPager(viewPager)

                    indicator.notifyDataSetChanged()
                    newAdapter.registerDataSetObserver(object : DataSetObserver() {
                        override fun onChanged() {
                            indicator.notifyDataSetChanged()
                        }
                    })
                }
            })
        } else {
            indicator.setViewPager(viewPager)
            indicator.notifyDataSetChanged()

            adapter.registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    indicator.notifyDataSetChanged()
                }
            })
        }
    }
}

fun PageIndicator.setupWithViewPager(viewPager: ViewPager) {
    viewPager.setTag(R.id.page_indicator_id, PageIndicatorMediator(this, viewPager))
}