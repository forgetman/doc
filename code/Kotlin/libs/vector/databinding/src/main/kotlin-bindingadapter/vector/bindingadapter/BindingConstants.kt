package vector.bindingadapter

import androidx.lifecycle.LiveData

const val ATTR_CHANGED_SUFFIX = "AttrChanged"
const val BINDING_PREFIX = "android:"

data class CurrentItem(val index: Int, val smoothScroll: Boolean? = null) {

    override fun equals(other: Any?): Boolean {
        if (other !is CurrentItem) return false
        return index == other.index && smoothScroll == other.smoothScroll
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + (smoothScroll?.hashCode() ?: 0)
        return result
    }
}

inline val LiveData<CurrentItem>.index: Int get() = value?.index ?: 0
inline val LiveData<CurrentItem>.smoothScroll: Boolean get() = value?.smoothScroll ?: false

abstract class ViewPagerParent {
    protected companion object {
        const val ON_PAGE_SCROLL_STATE_CHANGED = BINDING_PREFIX + "viewPager_onPageScrollStateChanged"
        const val ON_PAGE_SCROLLED = BINDING_PREFIX + "viewPager_onPageScrolled"
        const val ON_PAGE_SELECTED = BINDING_PREFIX + "viewPager_onPageSelected"
        const val ON_PAGE_DIRECTION = BINDING_PREFIX + "viewPager_onPageDirection"
        const val ON_PAGE_INTENT = BINDING_PREFIX + "viewPager_onPageIntent"

        const val ITEM_BINDER = BINDING_PREFIX + "viewPager_itemBinder"
        const val ITEM_DATA = BINDING_PREFIX + "viewPager_itemData"
        const val ITEM_CYCLE = BINDING_PREFIX + "viewPager_itemCycle"

        const val PAGER = BINDING_PREFIX + "viewPager_fragPager"
        const val ADAPTER = BINDING_PREFIX + "viewPager_fragAdapter"
        const val USE_STATE_ADAPTER = BINDING_PREFIX + "viewPager_useState"

        const val OFFSCREEN_PAGE_LIMIT = BINDING_PREFIX + "viewPager_offscreenPageLimit"

        const val CURRENT_ITEM = BINDING_PREFIX + "viewPager_currentItem"

        const val SCROLLABLE = BINDING_PREFIX + "viewPager_scrollable" // 是否可以滑动(手势)
        const val DURATION = BINDING_PREFIX + "viewPager_duration" // 滚动动画耗费时间
        const val SMOOTH_SCROLL = BINDING_PREFIX + "viewPager_smoothScroll" // 切换页面的时候是否开启动画

        const val AUTO_SCROLL = BINDING_PREFIX + "viewPager_autoScroll" // 是否开启定时自动滚动
        const val INTERVAL = BINDING_PREFIX + "viewPager_scrollIntervalInMillis" // 自动滚动间隔

        const val PAGE_TRANSFORMER = BINDING_PREFIX + "viewPager_transformer"

        const val TAB_LAYOUT_ID = BINDING_PREFIX + "viewPager_tabLayoutId"

        const val ORIENTATION = BINDING_PREFIX + "viewPager_orientation"

        const val ON_ITEM_CLICK = BINDING_PREFIX + "viewPager_onItemClick"
        const val ON_ITEM_DOUBLE_CLICK = BINDING_PREFIX + "viewPager_onItemDoubleClick"
        const val ON_ITEM_LONG_CLICK = BINDING_PREFIX + "viewPager_onItemLongClick"

        const val ITEM_COMPARE = BINDING_PREFIX + "viewPager_itemCompare"

        const val ON_DATA_CHANGED = BINDING_PREFIX + "viewPager_onDataChanged"

        const val OVER_SCROLL_NEVER = BINDING_PREFIX + "viewPager_overScrollNever"

        const val CLIP_TO_PADDING = BINDING_PREFIX + "viewPager_clipToPadding"
        const val PADDING_HORIZONTAL = BINDING_PREFIX + "viewPager_paddingHorizontal"
    }
}