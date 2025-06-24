@file:Suppress("DEPRECATION")

package vector.widget.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import sugar.ext.throwIfNull
import vector.app.adapter.pager.FragPager
import vector.widget.viewpager.ViewPager

open class FragStatePagerAdapter private constructor(manager: FragmentManager) :
    FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT),
    BaseViewPagerAdapter {

    constructor(activity: FragmentActivity) : this(activity.supportFragmentManager)
    constructor(fragment: Fragment) : this(fragment.childFragmentManager)

    private var fragPager: FragPager? = null
    private val dataCount: Int
        get() = fragPager?.size ?: 0

    private var modify = false

    override fun setData(p: FragPager, viewPager: ViewPager) {
        if (fragPager != null && fragPager != p) modify = true
        fragPager = p

        viewPager.notifyDataSetChanged()

        p.requiredCurrentItem?.let {
            viewPager.setCurrentItem(it, false)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragPager?.titles?.get(position)
    }

    override fun getItem(position: Int): Fragment {
        return fragPager?.createInstance(position).throwIfNull("Can not new a Fragment.")
    }

    override fun getCount(): Int {
        return dataCount
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (modify) {
            POSITION_NONE
        } else {
            POSITION_UNCHANGED
        }
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        modify = false
    }
}