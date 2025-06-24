@file:Suppress("unused")

package vector.widget.databinding.viewpager.binding

import android.content.Context
import androidx.annotation.IdRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import sugar.ext.cast
import sugar.ext.ifNotNull
import vector.app.adapter.pager.FragPager
import vector.app.ext.ResourceContext
import vector.bindingadapter.ATTR_CHANGED_SUFFIX
import vector.bindingadapter.CurrentItem
import vector.bindingadapter.ViewPagerParent
import vector.widget.compat.viewpager.ItemPagerBinder
import vector.widget.compat.viewpager.ViewPagerCompat
import vector.widget.databinding.viewpager.ViewPagerBind
import vector.widget.scrollable.adapter.ItemAdapter
import vector.widget.viewpager.ViewPager
import vector.widget.viewpager.adapter.BaseViewPagerAdapter
import vector.widget.viewpager.adapter.FragPagerAdapter
import vector.widget.viewpager.adapter.FragStatePagerAdapter
import vector.widget.viewpager.adapter.ItemPagerAdapter

/**
 * @author yuansui
 * @since 2018/11/11
 */
object ViewPagerBinding : ViewPagerParent() {

    @JvmStatic
    @BindingAdapter(PAGE_TRANSFORMER)
    fun setPageTransformer(
        view: ViewPager,
        transformer: androidx.viewpager.widget.ViewPager.PageTransformer?
    ) {
        view.setPageTransformer(false, transformer)
    }

    @JvmStatic
    @BindingAdapter(INTERVAL)
    fun setInterval(view: ViewPager, interval: Long) {
        view.interval = interval
    }

    @JvmStatic
    @BindingAdapter(AUTO_SCROLL)
    fun setAutoScroll(view: ViewPager, enable: Boolean) {
        view.setAutoScroll(enable)
    }

    @JvmStatic
    @BindingAdapter(DURATION)
    fun setDuration(view: ViewPager, duration: Int) {
        view.setScrollDuration(duration)
    }

    @JvmStatic
    @BindingAdapter(SCROLLABLE)
    fun setScrollable(view: ViewPager, scrollable: Boolean) {
        view.isScrollable = scrollable
    }

    @JvmStatic
    @BindingAdapter(SMOOTH_SCROLL)
    fun setSmoothScroll(view: ViewPager, smoothScroll: Boolean) {
        view.smoothScroll = smoothScroll
    }

    @JvmStatic
    @BindingAdapter(CURRENT_ITEM)
    fun setCurrentItem(view: ViewPager, item: CurrentItem) {
        if (view.currentItem == item.index) return
        view.setCurrentItem(item.index, item.smoothScroll ?: view.smoothScroll)
    }

    @JvmStatic
    @BindingAdapter(CURRENT_ITEM)
    fun setCurrentItem(view: ViewPager, item: Int) {
        if (view.currentItem == item) return
        view.setCurrentItem(item, view.smoothScroll)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CURRENT_ITEM)
    fun getCurrentItem(view: ViewPager): CurrentItem {
        return CurrentItem(view.currentItem, view.smoothScroll)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CURRENT_ITEM)
    fun getCurrentItemInt(view: ViewPager): Int {
        return view.currentItem
    }

    @JvmStatic
    @BindingAdapter(
        ON_PAGE_SCROLL_STATE_CHANGED,
        ON_PAGE_SCROLLED,
        ON_PAGE_SELECTED,
        ON_PAGE_DIRECTION,
        ON_PAGE_INTENT,
        CURRENT_ITEM + ATTR_CHANGED_SUFFIX,
        requireAll = false
    )
    fun setOnPageChangeListener(
        view: ViewPager,
        onScrollStateChanged: ViewPagerBind.OnPageScrollStateChanged?,
        onScrolled: ViewPagerBind.OnPageScrolled?,
        onSelected: ViewPagerBind.OnPageSelected?,
        onDirection: ViewPagerBind.OnPageDirection?,
        onIntent: ViewPagerBind.OnPageIntent?,
        attrChanged: InverseBindingListener?
    ) {
        val listener = ViewPagerCompat.OnPageChangedListener.newBuilder {
            onScrollStateChanged.ifNotNull {
                this.onScrollStateChanged = { state ->
                    it.action(state)
                }
            }

            onScrolled.ifNotNull {
                this.onScrolled =
                    { currPosition, nextPosition, positionOffset, positionOffsetPixels ->
                        it.action(currPosition, nextPosition, positionOffset, positionOffsetPixels)
                    }
            }

            if (onSelected != null || attrChanged != null) {
                this.onSelected = { position ->
                    onSelected?.action?.invoke(position)
                    attrChanged?.onChange()
                }
            }

            onDirection.ifNotNull {
                this.onDirection = { scroll, slide ->
                    it.action(scroll, slide)
                }
            }

            onIntent.ifNotNull {
                this.onIntent = { position ->
                    it.action(position)
                }
            }
        }.build()

        view.addOnPageChangeListener(listener)
    }

    @JvmStatic
    @BindingAdapter(TAB_LAYOUT_ID)
    fun setTabLayout(view: ViewPager, @IdRes id: Int) {
        val layout = view.rootView?.findViewById<TabLayout>(id) ?: return
        layout.setupWithViewPager(view)
    }

    @JvmStatic
    @BindingAdapter(OFFSCREEN_PAGE_LIMIT)
    fun setOffscreenPageLimit(view: ViewPager, limit: Int) {
        if (limit > 0) view.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter(
        ADAPTER, PAGER, USE_STATE_ADAPTER,
        requireAll = false
    )
    @Throws(NullPointerException::class)
    fun <A : PagerAdapter> setFragPagerAdapter(
        view: ViewPager,
        adapter: A?,
        fragPager: FragPager?,
        useState: Boolean?,
    ) {
        val old = view.adapter
        val realAdapter = if (old == null) {
            val newAdapter = adapter ?: if (useState == true) {
                getFragStateAdapter(view.context)
            } else {
                getFragAdapter(view.context)
            }
            view.adapter = newAdapter
            newAdapter
        } else {
            old
        }

        realAdapter.cast<BaseViewPagerAdapter> {
            if (fragPager != null) it.setData(fragPager, view)
        }
    }

    @JvmStatic
    @BindingAdapter(ITEM_DATA, ITEM_BINDER, ITEM_CYCLE, requireAll = false)
    fun <T : Any> setItemBinders(
        view: ViewPager,
        data: List<T>?,
        binders: List<ItemPagerBinder<*, *>>,
        enableCycle: Boolean?
    ) {
        nativeSetItemBinders(view, data, binders, enableCycle)
    }

    @JvmStatic
    @BindingAdapter(ITEM_DATA, ITEM_BINDER, ITEM_CYCLE, requireAll = false)
    fun <T : Any> setItemBinder(
        view: ViewPager,
        data: List<T>?,
        binder: ItemPagerBinder<*, *>,
        enableCycle: Boolean?
    ) {
        nativeSetItemBinders(view, data, listOf(binder), enableCycle)
    }

    private fun <T : Any> nativeSetItemBinders(
        view: ViewPager,
        data: List<T>?,
        binders: List<ItemPagerBinder<*, *>>,
        enableCycle: Boolean?
    ) {
        if (view.adapter == null) {
            val adapter = ItemPagerAdapter()
            adapter.registerItemBinders(binders)
            view.adapter = adapter
            enableCycle?.let {
                adapter.itemCycle = it
                if (it) view.setCurrentItem(Int.MAX_VALUE / 2, false)
            }
        }

        view.adapter.cast<ItemAdapter> {
            it.data = data
        }
    }

    @Throws(NullPointerException::class)
    private fun getFragAdapter(context: Context): PagerAdapter {
        return when (context) {
            is FragmentActivity -> FragPagerAdapter(context)
            is ResourceContext -> getFragAdapter(context.baseContext)
            is ContextThemeWrapper -> getFragAdapter(context.baseContext)
            else -> throw NullPointerException("Context is not a FragmentActivity")
        }
    }

    @Throws(NullPointerException::class)
    private fun getFragStateAdapter(context: Context): PagerAdapter {
        return when (context) {
            is FragmentActivity -> FragStatePagerAdapter(context)
            is ResourceContext -> getFragStateAdapter(context.baseContext)
            is ContextThemeWrapper -> getFragStateAdapter(context.baseContext)
            else -> throw NullPointerException("Context is not a FragmentActivity")
        }
    }
}