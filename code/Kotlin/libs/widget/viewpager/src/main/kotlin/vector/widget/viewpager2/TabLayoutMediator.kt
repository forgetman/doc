package vector.widget.viewpager2

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.google.android.material.tabs.TabLayout
import sugar.ext.cast
import vector.widget.viewpager2.adapter.FragStatePagerAdapter2
import java.lang.ref.WeakReference

/**
 * 参照[com.google.android.material.tabs.TabLayoutMediator]源码
 * 修改如下:
 * 1. 去掉autoRefresh的设置, 默认开启
 * 2. 考虑到DataBinding, 添加adapter设置监听机制
 * 3. 重复attach不会抛出异常
 * 4. 从[FragStatePagerAdapter2]里获取title作为text
 * 5. 去掉 onConfigureTabCallback
 */
class TabLayoutMediator(private val tabLayout: TabLayout, private val viewPager: ViewPager2) {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached: Boolean = false

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null

    init {
        adapter = viewPager.adapter
        if (adapter == null) {
            viewPager.accessibilityProvider = object : ViewPager2.AccessibilityProvider() {

                override fun onAttachAdapter(newAdapter: RecyclerView.Adapter<*>?) {
                    if (newAdapter == null) return
                    adapter = newAdapter
                    attach()
                }

                override fun onDetachAdapter(oldAdapter: RecyclerView.Adapter<*>?) {
                    if (oldAdapter != null) detach()
                }
            }
        } else {
            attach()
        }
    }

    fun attach() {
        if (attached) {
            return
        }
        attached = true

        onPageChangeCallback = TabLayoutOnPageChangeCallback(tabLayout).apply {
            viewPager.registerOnPageChangeCallback(this)
        }

        onTabSelectedListener = ViewPagerOnTabSelectedListener(viewPager).apply {
            tabLayout.addOnTabSelectedListener(this)
        }

        pagerAdapterObserver = PagerAdapterObserver().apply {
            adapter?.registerAdapterDataObserver(this)
        }

        populateTabsFromPagerAdapter()

        // Now update the scroll position to match the ViewPager's current item
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)
    }

    fun detach() {
        if (!attached) return

        pagerAdapterObserver?.let {
            adapter?.unregisterAdapterDataObserver(it)
            pagerAdapterObserver = null
        }

        onTabSelectedListener?.let {
            tabLayout.removeOnTabSelectedListener(it)
            onTabSelectedListener = null
        }

        onPageChangeCallback?.let {
            viewPager.unregisterOnPageChangeCallback(it)
            onPageChangeCallback = null
        }

        attached = false
    }

    private fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()

        if (adapter != null) {
            val adapterCount = adapter?.itemCount ?: 0
            for (i in 0 until adapterCount) {
                val tab = tabLayout.newTab()
                adapter.cast<FragStatePagerAdapter2> {
                    tab.text = it.getPageTitle(i)
                }
                tabLayout.addTab(tab, false)
            }

            // Make sure we reflect the currently set ViewPager item
            if (adapterCount > 0) {
                val currItem = viewPager.currentItem
                if (currItem != tabLayout.selectedTabPosition) {
                    tabLayout.getTabAt(currItem)?.select()
                }
            }
        }
    }

    /**
     * A [OnPageChangeCallback] class which contains the necessary calls back to the
     * provided [TabLayout] so that the tab position is kept in sync.
     *
     *
     * This class stores the provided TabLayout weakly, meaning that you can use [androidx.viewpager2.widget.ViewPager2.registerOnPageChangeCallback] without removing the
     * callback and not cause a leak.
     */
    private class TabLayoutOnPageChangeCallback(tabLayout: TabLayout) :
        OnPageChangeCallback() {
        private val tabLayoutRef: WeakReference<TabLayout> = WeakReference(tabLayout)
        private var previousScrollState: Int = 0
        private var scrollState: Int = 0

        init {
            reset()
        }

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null) {
                // Only update the text selection if we're not settling, or we are settling after
                // being dragged
                val updateText =
                    scrollState != SCROLL_STATE_SETTLING || previousScrollState == SCROLL_STATE_DRAGGING
                // Update the indicator if we're not settling after being idle. This is caused
                // from a setCurrentItem() call and will be handled by an animation from
                // onPageSelected() instead.
                val updateIndicator =
                    !(scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE)
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null
                && tabLayout.selectedTabPosition != position
                && position < tabLayout.tabCount
            ) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                val updateIndicator =
                    scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
            }
        }

        fun reset() {
            scrollState = SCROLL_STATE_IDLE
            previousScrollState = SCROLL_STATE_IDLE
        }
    }

    /**
     * A [TabLayout.OnTabSelectedListener] class which contains the necessary calls back to the
     * provided [ViewPager2] so that the tab position is kept in sync.
     */
    private class ViewPagerOnTabSelectedListener(private val viewPager: ViewPager2) :
        TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.currentItem = tab.position
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // No-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            // No-op
        }
    }

    private inner class PagerAdapterObserver :
        RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }
    }
}

fun TabLayout.setupWithViewPager(viewPager: ViewPager2) {
    viewPager.mediator = TabLayoutMediator(this, viewPager)
}