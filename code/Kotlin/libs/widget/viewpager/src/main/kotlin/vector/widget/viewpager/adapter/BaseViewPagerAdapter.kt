package vector.widget.viewpager.adapter

import vector.app.adapter.pager.FragPager
import vector.widget.viewpager.ViewPager

interface BaseViewPagerAdapter {
    fun setData(p: FragPager, viewPager: ViewPager)
}