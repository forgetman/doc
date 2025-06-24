@file:Suppress("unused")

package vector.widget.databinding.viewpager.binding

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import sugar.ext.cast
import sugar.ext.ifNotNull
import sugar.ext.lifecycle
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.ItemPager
import vector.app.ext.ResourceContext
import vector.bindingadapter.ATTR_CHANGED_SUFFIX
import vector.bindingadapter.BINDING_PREFIX
import vector.bindingadapter.CurrentItem
import vector.bindingadapter.ViewPagerParent
import vector.widget.compat.viewpager.ViewPagerCompat
import vector.widget.databinding.viewpager.ViewPagerBind
import vector.widget.scrollable.adapter.ItemCompare
import vector.widget.scrollable.adapter.binder.ItemBinder
import vector.widget.viewpager2.ViewPager2
import vector.widget.viewpager2.adapter.FragStatePagerAdapter2
import vector.widget.viewpager2.adapter.ItemPagerAdapter2
import vector.widget.viewpager2.setupWithViewPager

import androidx.viewpager2.widget.ViewPager2 as XViewPager2

/**
 * @author yuansui
 * @since 2019-07-18
 */
object ViewPager2Binding : ViewPagerParent() {

    private const val VIEW_CACHE_SIZE = BINDING_PREFIX + "viewPager_viewCacheSize"
    private const val VIEW_ITEM_ANIMATOR = BINDING_PREFIX + "viewPager_itemAnimator"

    @JvmStatic
    @BindingAdapter(VIEW_ITEM_ANIMATOR)
    fun setItemAnimator(view: ViewPager2, animator: RecyclerView.ItemAnimator?) {
        view.setItemAnimator(animator)
    }

    @JvmStatic
    @BindingAdapter(VIEW_CACHE_SIZE)
    fun setItemViewCacheSize(view: ViewPager2, size: Int) {
        view.setItemViewCacheSize(size)
    }

    @JvmStatic
    @BindingAdapter(OVER_SCROLL_NEVER)
    fun setOverScrollNever(view: ViewPager2, never: Boolean) {
        if (never) {
            view.overScrollMode = View.OVER_SCROLL_NEVER
        } else {
            view.overScrollMode = View.OVER_SCROLL_ALWAYS
        }
    }

    @JvmStatic
    @BindingAdapter(PAGE_TRANSFORMER)
    fun setPageTransformer(
        view: ViewPager2,
        transformer: XViewPager2.PageTransformer?
    ) {
        view.setPageTransformer(transformer)
    }

    @JvmStatic
    @BindingAdapter(SMOOTH_SCROLL)
    fun setSmoothScroll(view: ViewPager2, smoothScroll: Boolean) {
        view.smoothScroll = smoothScroll
    }

    @JvmStatic
    @BindingAdapter(CURRENT_ITEM)
    fun setCurrentItem(view: ViewPager2, item: CurrentItem?) {
        if (item == null) return
        if (view.currentItem == item.index) return
        view.setCurrentItem(item.index, item.smoothScroll)
    }

    @JvmStatic
    @BindingAdapter(CURRENT_ITEM)
    fun setCurrentItem(view: ViewPager2, item: Int) {
        if (view.currentItem == item) return
        view.currentItem = item
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CURRENT_ITEM)
    fun getCurrentItem(view: ViewPager2): CurrentItem {
        return CurrentItem(view.currentItem, view.smoothScroll)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CURRENT_ITEM)
    fun getCurrentItemInt(view: ViewPager2): Int {
        return view.currentItem
    }

    @JvmStatic
    @BindingAdapter(ITEM_COMPARE)
    fun setItemComparison(view: ViewPager2, comparison: ItemCompare) {
        view.itemComparison = comparison
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
        view: ViewPager2,
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
                this.onDirection = { page, slide ->
                    it.action(page, slide)
                }
            }

            onIntent.ifNotNull {
                this.onIntent = { position ->
                    it.action(position)
                }
            }
        }.build()

        view.registerOnPageChangeCallback(listener)
    }

    @JvmStatic
    @BindingAdapter(SCROLLABLE)
    fun setScrollable(view: ViewPager2, scrollable: Boolean?) {
        scrollable ?: return
        view.isScrollable = scrollable
    }

    @JvmStatic
    @BindingAdapter(OFFSCREEN_PAGE_LIMIT)
    fun setOffscreenPageLimit(view: ViewPager2, @XViewPager2.OffscreenPageLimit limit: Int) {
        view.offscreenPageLimit = limit
    }

    @JvmStatic
    @BindingAdapter(ORIENTATION)
    fun setOrientation(view: ViewPager2, @XViewPager2.Orientation orientation: Int) {
        view.orientation = orientation
    }

    @JvmStatic
    @BindingAdapter(TAB_LAYOUT_ID)
    fun setTabLayout(view: ViewPager2, @IdRes id: Int) {
        view.rootView?.findViewById<TabLayout>(id)?.setupWithViewPager(view)
    }

    @JvmStatic
    @BindingAdapter(CLIP_TO_PADDING)
    fun setClipToPadding(view: ViewPager2, clipToPadding: Boolean) {
        view.setInnerClipToPadding(clipToPadding)
    }

    @JvmStatic
    @BindingAdapter(PADDING_HORIZONTAL)
    fun setPaddingHorizontal(view: ViewPager2, padding: Int) {
        view.setInnerPaddingHorizontal(padding)
    }

    @JvmStatic
    @BindingAdapter(
        ITEM_DATA,
        ITEM_BINDER,
        ITEM_CYCLE,
        ON_ITEM_CLICK,
        ON_ITEM_DOUBLE_CLICK,
        ON_ITEM_LONG_CLICK,
        ON_DATA_CHANGED,
        requireAll = false
    )
    fun <T : Any> setItemBinders(
        view: ViewPager2,
        data: ItemPager<T>,
        binders: List<ItemBinder<*, *>>,
        enableCycle: Boolean?,
        onItemClick: ViewPagerBind.OnItemClick?,
        onItemDoubleClick: ViewPagerBind.OnItemDoubleClick?,
        onItemLongClick: ViewPagerBind.OnItemLongClick?,
        onDataChanged: ViewPagerBind.OnDataChanged?
    ) {
        nativeSetItemBinders(
            view,
            data.data,
            data.requiredCurrentItem,
            binders,
            enableCycle,
            onItemClick,
            onItemDoubleClick,
            onItemLongClick,
            onDataChanged
        )
    }

    @JvmStatic
    @BindingAdapter(
        ITEM_DATA,
        ITEM_BINDER,
        ITEM_CYCLE,
        ON_ITEM_CLICK,
        ON_ITEM_DOUBLE_CLICK,
        ON_ITEM_LONG_CLICK,
        ON_DATA_CHANGED,
        requireAll = false
    )
    fun <T : Any> setItemBinders(
        view: ViewPager2,
        data: List<T>?,
        binders: List<ItemBinder<*, *>>,
        enableCycle: Boolean?,
        onItemClick: ViewPagerBind.OnItemClick?,
        onItemDoubleClick: ViewPagerBind.OnItemDoubleClick?,
        onItemLongClick: ViewPagerBind.OnItemLongClick?,
        onDataChanged: ViewPagerBind.OnDataChanged?
    ) {
        nativeSetItemBinders(
            view,
            data,
            null,
            binders,
            enableCycle,
            onItemClick,
            onItemDoubleClick,
            onItemLongClick,
            onDataChanged
        )
    }

    @JvmStatic
    @BindingAdapter(
        ITEM_DATA,
        ITEM_BINDER,
        ITEM_CYCLE,
        ON_ITEM_CLICK,
        ON_ITEM_DOUBLE_CLICK,
        ON_ITEM_LONG_CLICK,
        ON_DATA_CHANGED,
        requireAll = false
    )
    fun <T : Any> setItemBinder(
        view: ViewPager2,
        data: List<T>?,
        binder: ItemBinder<*, *>,
        enableCycle: Boolean?,
        onItemClick: ViewPagerBind.OnItemClick?,
        onItemDoubleClick: ViewPagerBind.OnItemDoubleClick?,
        onItemLongClick: ViewPagerBind.OnItemLongClick?,
        onDataChanged: ViewPagerBind.OnDataChanged?
    ) {
        nativeSetItemBinders(
            view,
            data,
            null,
            listOf(binder),
            enableCycle,
            onItemClick,
            onItemDoubleClick,
            onItemLongClick,
            onDataChanged
        )
    }

    private fun <T : Any> nativeSetItemBinders(
        view: ViewPager2,
        data: List<T>?,
        requiredCurrentItem: Int?,
        binders: List<ItemBinder<*, *>>,
        enableCycle: Boolean?,
        onItemClick: ViewPagerBind.OnItemClick?,
        onItemDoubleClick: ViewPagerBind.OnItemDoubleClick?,
        onItemLongClick: ViewPagerBind.OnItemLongClick?,
        onDataChanged: ViewPagerBind.OnDataChanged?
    ) {
        if (view.adapter == null) {
            val adapter = ItemPagerAdapter2()
            adapter.registerItemBinders(binders)
            adapter.data = data

            var observer: RecyclerView.AdapterDataObserver? = null
            if (onDataChanged != null) {
                observer = object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        onDataChanged.action.invoke()
                    }
                }
                adapter.registerAdapterDataObserver(observer)
            }

            view.adapter = adapter
            enableCycle?.let {
                adapter.itemCycle = it
                if (it) view.setCurrentItem(Int.MAX_VALUE / 2, false)
            }

            if (onItemClick != null) {
                adapter.setOnItemClickListener { itemView, position ->
                    onItemClick.action(itemView, position)
                }
            }

            if (onItemDoubleClick != null) {
                adapter.setOnItemDoubleClickListener { itemView, position ->
                    onItemDoubleClick.action(itemView, position)
                }
            }

            if (onItemLongClick != null) {
                adapter.setOnItemLongClickListener { itemView, position ->
                    onItemLongClick.action(itemView, position)
                }
            }

            view.lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        if (observer != null) {
                            adapter.unregisterAdapterDataObserver(observer)
                        }
                        // 需要手动置空, 不然一些列的on detach消息不会进行回调
                        view.adapter = null
                    }
                }
            })
        } else {
            view.adapter.cast<ItemPagerAdapter2> { itemAdapter ->
                itemAdapter.data = data
                requiredCurrentItem?.let {
                    view.setCurrentItem(it, false)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(ADAPTER, PAGER, requireAll = false)
    fun setFragAdapter(
        view: ViewPager2,
        adapter: FragStatePagerAdapter2?,
        fragPager: FragPager?
    ) {
        val old = view.adapter
        if (old == null) {
            val newAdapter = adapter ?: getAdapter(view.context)
            if (fragPager != null) newAdapter.setData(fragPager, view)
            view.adapter = newAdapter
            view.lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        // 需要手动置空, 不然一些列的on detach消息不会进行回调
                        view.adapter = null
                    }
                }
            })
        } else {
            old.cast<FragStatePagerAdapter2> {
                if (fragPager != null) it.setData(fragPager, view)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(NullPointerException::class)
    private fun <T : FragStatePagerAdapter2> getAdapter(context: Context): T =
        when (context) {
            is ResourceContext -> getAdapter(context.baseContext)
            is FragmentActivity -> FragStatePagerAdapter2(context)
            else -> throw NullPointerException("Context is not a Fragment or FragmentActivity")
        } as T
}