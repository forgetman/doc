package vector.widget.databinding.scrollable.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import vector.app.delegate.OnScrollCompatListener
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.databinding.scrollable.binding.trigger.ScrollByTrigger
import vector.widget.databinding.scrollable.binding.trigger.ScrollToPositionTrigger
import vector.widget.databinding.scrollable.binding.trigger.ScrollToTopTrigger
import vector.widget.databinding.scrollable.binding.trigger.ScrollableTrigger
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.scrollable.ListView
import vector.widget.scrollable.adapter.ItemAdapter
import vector.widget.scrollable.adapter.ItemCompare
import vector.widget.scrollable.adapter.binder.EmptyItemBinder
import vector.widget.scrollable.adapter.binder.ItemBinder
import vector.widget.scrollable.layoutmanager.LayoutManagers
import vector.widget.swiperefresh.delegate.LoadMore

/**
 * @author yuansui
 * @since 2018/3/7
 */
object ListViewBinding {

    private const val ON_SCROLL = BINDING_PREFIX + "listView_onScroll"
    private const val ON_SCROLL_STATE_CHANGED = BINDING_PREFIX + "listView_onScrollStateChanged"

    private const val ON_ITEM_CLICK = BINDING_PREFIX + "listView_onItemClick"
    private const val ON_ITEM_DOUBLE_CLICK = BINDING_PREFIX + "listView_onItemDoubleClick"
    private const val ON_ITEM_LONG_CLICK = BINDING_PREFIX + "listView_onItemLongClick"

    private const val ITEM_DATA = BINDING_PREFIX + "listView_itemData"
    private const val ITEM_BINDER = BINDING_PREFIX + "listView_itemBinder"
    private const val EMPTY_ITEM_BINDER = BINDING_PREFIX + "listView_itemBinderEmpty"
    private const val ITEM_COMPARE = BINDING_PREFIX + "listView_itemCompare"

    private const val VIEW_CACHE_SIZE = BINDING_PREFIX + "listView_viewCacheSize"

    private const val DECORATION = BINDING_PREFIX + "listView_decoration"

    private const val ON_LOAD_MORE = BINDING_PREFIX + "listView_onLoadMore"

    private const val SCROLL_TO_POSITION = BINDING_PREFIX + "listView_scrollToPosition"
    private const val SMOOTH_SCROLL = BINDING_PREFIX + "listView_smoothScroll"

    private const val LAYOUT_MANAGER = BINDING_PREFIX + "listView_layoutManager"
    private const val ITEM_TOUCH_HELPER = BINDING_PREFIX + "listView_itemTouchHelper"

    //    private const val HAS_STABLE_IDS = BINDING_PREFIX + "listView_hasStableIds"
    private const val NESTED_SCROLLING_ENABLED = BINDING_PREFIX + "listView_nestedScrollingEnabled"
    private const val SCROLLABLE_HEIGHT = BINDING_PREFIX + "listView_scrollableHeight"

    private const val PRELOAD = BINDING_PREFIX + "listView_preload"

    private const val TRIGGER = BINDING_PREFIX + "listView_trigger"

    private const val DATA_OBSERVER = BINDING_PREFIX + "listView_dataObserver"

    @JvmStatic
    @BindingAdapter(DECORATION)
    fun setDecoration(view: ListView, decoration: RecyclerView.ItemDecoration?) {
        view.clearItemDecorations()
        if (decoration != null) {
            view.addItemDecoration(decoration)
        }
    }

    @JvmStatic
    @BindingAdapter(ON_SCROLL, ON_SCROLL_STATE_CHANGED, requireAll = false)
    fun setOnScroll(
        view: ListView,
        onScroll: ScrollableBind.List.OnScroll?,
        onStateChanged: ScrollableBind.List.OnScrollStateChanged?
    ) {
        view.addOnScrollListener(object : OnScrollCompatListener() {
            private var state: Int = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                state = newState
                onStateChanged?.action?.invoke(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScroll?.action?.invoke(recyclerView, dx, dy, state)
            }
        })
    }

    @JvmStatic
    @BindingAdapter(ON_LOAD_MORE, PRELOAD, requireAll = false)
    fun setOnLoadMore(view: ListView, onLoadMore: RefreshBind.OnLoadMore?, preLoad: Boolean?) {
        onLoadMore?.run {
            view.setOnLoadMoreListener(object : LoadMore.Listener {
                override fun onLoading(lastState: LoadMore.State) {
                    this@run.action(view.delegate, lastState)
                }
            })
            preLoad?.let { view.preLoad = it }
        }
    }

    @JvmStatic
    @BindingAdapter(SCROLL_TO_POSITION)
    fun setSelectedItemPosition(view: ListView, position: Int) {
        view.scrollToPosition(position, null)
    }

    @JvmStatic
    @BindingAdapter(SMOOTH_SCROLL)
    fun setSmoothScroll(view: ListView, smoothScroll: Boolean) {
        view.smoothScroll = smoothScroll
    }

    @JvmStatic
    @BindingAdapter(LAYOUT_MANAGER)
    fun setLayoutManager(view: ListView, factory: LayoutManagers.LayoutManagerFactory?) {
        val manager = factory?.create(view.context) ?: return
        if (manager !is StaggeredGridLayoutManager) view.setHasFixedSize(true)
        view.layoutManager = manager
    }

    @JvmStatic
    @BindingAdapter(NESTED_SCROLLING_ENABLED)
    fun setNestedScrollingEnabled(view: ListView, isEnabled: Boolean) {
        view.isNestedScrollingEnabled = isEnabled
    }

    @JvmStatic
    @BindingAdapter(SCROLLABLE_HEIGHT)
    fun setScrollableHeight(view: ListView, height: Int) {
        view.setScrollableHeight(height)
    }

    @JvmStatic
    @BindingAdapter(TRIGGER)
    fun setTrigger(view: ListView, trigger: ScrollableTrigger) {
        when (trigger) {
            is ScrollByTrigger -> {
                trigger.observe { x, y, smoothScroll ->
                    if (smoothScroll ?: view.smoothScroll) {
                        view.smoothScrollBy(x, y)
                    } else {
                        view.scrollBy(x, y)
                    }
                }
            }

            is ScrollToPositionTrigger -> {
                trigger.observe { position, smoothScroll ->
                    view.scrollToPosition(position, smoothScroll)
                }
            }

            is ScrollToTopTrigger -> {
                trigger.observe { smooth ->
                    view.scrollToPosition(0, smooth)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(TRIGGER)
    fun setTriggers(view: ListView, vararg triggers: ScrollableTrigger) {
        triggers.forEach {
            setTrigger(view, it)
        }
    }

    @JvmStatic
    @BindingAdapter(VIEW_CACHE_SIZE)
    fun setItemViewCacheSize(view: ListView, size: Int) {
        view.setItemViewCacheSize(size)
    }

    @JvmStatic
    @BindingAdapter(ITEM_COMPARE)
    fun setItemComparison(view: ListView, comparison: ItemCompare) {
        view.itemComparison = comparison
    }

    @JvmStatic
    @BindingAdapter(ITEM_TOUCH_HELPER)
    fun setItemTouchHelper(view: ListView, helper: ItemTouchHelper?) {
        helper?.attachToRecyclerView(view)
    }

    @JvmStatic
    @BindingAdapter(
        ITEM_DATA,
        ITEM_BINDER,
        EMPTY_ITEM_BINDER,
        ON_ITEM_CLICK,
        ON_ITEM_DOUBLE_CLICK,
        ON_ITEM_LONG_CLICK,
        DATA_OBSERVER,
        requireAll = false
    )
    fun <T : Any> setItemBinders(
        view: ListView,
        data: List<T>?,
        binders: List<ItemBinder<*, *>>,
        emptyItemBinder: EmptyItemBinder<*>?,
        onItemClick: ScrollableBind.List.OnItemClick?,
        onItemDoubleClick: ScrollableBind.List.OnItemDoubleClick?,
        onItemLongClick: ScrollableBind.List.OnItemLongClick?,
        dataObserver: RecyclerView.AdapterDataObserver?
    ) {
        nativeSetItemBinders(
            view,
            data,
            binders,
            emptyItemBinder,
            onItemClick,
            onItemDoubleClick,
            onItemLongClick,
            dataObserver
        )
    }

    @JvmStatic
    @BindingAdapter(
        ITEM_DATA,
        ITEM_BINDER,
        EMPTY_ITEM_BINDER,
        ON_ITEM_CLICK,
        ON_ITEM_DOUBLE_CLICK,
        ON_ITEM_LONG_CLICK,
        DATA_OBSERVER,
        requireAll = false
    )
    fun <T : Any> setItemBinders(
        view: ListView,
        data: List<T>?,
        binder: ItemBinder<*, *>,
        emptyItemBinder: EmptyItemBinder<*>?,
        onItemClick: ScrollableBind.List.OnItemClick?,
        onItemDoubleClick: ScrollableBind.List.OnItemDoubleClick?,
        onItemLongClick: ScrollableBind.List.OnItemLongClick?,
        dataObserver: RecyclerView.AdapterDataObserver?
    ) {
        nativeSetItemBinders(
            view,
            data,
            listOf(binder),
            emptyItemBinder,
            onItemClick,
            onItemDoubleClick,
            onItemLongClick,
            dataObserver
        )
    }

    private fun <T : Any> nativeSetItemBinders(
        view: ListView,
        data: List<T>?,
        binders: List<ItemBinder<*, *>>,
        emptyItemBinder: EmptyItemBinder<*>?,
        onItemClick: ScrollableBind.List.OnItemClick?,
        onItemDoubleClick: ScrollableBind.List.OnItemDoubleClick?,
        onItemLongClick: ScrollableBind.List.OnItemLongClick?,
        dataObserver: RecyclerView.AdapterDataObserver?
    ) {
        if (view.adapter == null) {
            val adapter = ItemAdapter()
            adapter.registerItemBinders(binders)
            if (dataObserver != null) {
                adapter.registerAdapterDataObserver(dataObserver)
            }
            view.setAdapters(adapter, emptyItemBinder)

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
        }

        view.itemAdapter?.data = data
    }
}