package vector.widget.scrollable

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import logger.L
import sugar.ext.cast
import vector.app.config.Config
import vector.app.ext.view.ensureIdExist
import vector.app.ext.view.setHeight
import vector.widget.scrollable.adapter.EmptyAdapter
import vector.widget.scrollable.adapter.ItemAdapter
import vector.widget.scrollable.adapter.ItemCompare
import vector.widget.scrollable.adapter.LoadMoreAdapter
import vector.widget.scrollable.adapter.binder.EmptyItemBinder
import vector.widget.scrollable.delegate.SpanSizeDelegate
import vector.widget.scrollable.layoutmanager.LayoutManagers
import vector.widget.swiperefresh.delegate.LoadMore

open class ListView @JvmOverloads constructor(
    context: Context, val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var smoothScroll = false
    private val scroller by lazy { StartScroller(context) }

    var preLoad: Boolean = false
        set(value) {
            field = value
            if (delegateLazy.isInitialized()) {
                delegate.preLoad = value
            }
        }

    private val delegateLazy = lazy { LoadMoreAdapter() }
    val delegate by delegateLazy
    var itemAdapter: ItemAdapter? = null
    var itemComparison: ItemCompare = ItemCompare.RANGE_CHANGED
        set(value) {
            field = value
            itemAdapter?.itemCompare = value
        }

    lateinit var empty: EmptyAdapter
    private val emptyObserver by lazy(LazyThreadSafetyMode.NONE) {
        object : AdapterDataObserver() {

            private fun notifyChanged() {
                val currItemAdapter = itemAdapter
                when {
                    currItemAdapter == null -> empty.isEmpty = true
                    currItemAdapter.itemCount == 0 -> empty.isEmpty = true
                    else -> empty.isEmpty = false
                }
            }

            override fun onChanged() {
                notifyChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                notifyChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notifyChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                notifyChanged()
            }
        }
    }

    init {
        ensureIdExist()
        overScrollMode = Config.list().overScrollMode
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        // grid和瀑布流都继承于linear, flexbox和自定义的除外
        if (layout !is LinearLayoutManager) return
        setSpanSize(layout)
    }

    fun setAdapters(adapter: ItemAdapter, emptyItemBinder: EmptyItemBinder<*>?): ConcatAdapter {
        adapter.itemCompare = itemComparison
        itemAdapter = adapter

        val adapters = mutableListOf<Adapter<*>>()
        adapters.add(adapter)
        if (delegateLazy.isInitialized()) {
            adapters.add(delegate)
            adapter.registerAdapterDataObserver(delegate.dataObserver)
            delegate.preLoad = preLoad
        }

        if (emptyItemBinder != null) {
            empty = EmptyAdapter()
            empty.binder = emptyItemBinder
            adapters.add(empty)
            adapter.registerAdapterDataObserver(emptyObserver)
        }

        var oldManager = layoutManager
        if (oldManager == null) {
            // 如果没有设置manager, 设置一个默认的
            oldManager = LayoutManagers.linear().create(context)
            setHasFixedSize(true)
            layoutManager = oldManager
        } else {
            setSpanSize(oldManager)
        }

        val concatAdapter = ConcatAdapter(adapters)
        super.setAdapter(concatAdapter)
        return concatAdapter
    }

    fun setOnLoadMoreListener(listener: LoadMore.Listener) {
        if (delegateLazy.isInitialized()) return
        delegate.setListener(listener)
        delegate.ready()
    }

    private fun findAdapterIndex(position: Int): Int {
        var realPos: Int = position
        val ca = adapter as ConcatAdapter
        ca.adapters.forEachIndexed { index, adapter ->
            val count = adapter.itemCount
            if (realPos >= count) {
                realPos -= count
            } else {
                return index
            }
        }
        return -1
    }

    private fun setSpanSize(layout: LayoutManager?) {
        layout.cast<GridLayoutManager> { manager ->
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val index = findAdapterIndex(position)
                    if (index == -1) return 1

                    val ca = adapter as? ConcatAdapter ?: return 1
                    ca.adapters[index].cast<SpanSizeDelegate> {
                        return it.getSpanSize(position, manager.spanCount)
                    }
                    return 1
                }
            }
        }
    }

    /**
     * 清除所有分割线
     */
    fun clearItemDecorations() {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
    }

    fun scrollToPosition(position: Int, smoothScroll: Boolean?) {
        if (smoothScroll ?: this.smoothScroll) {
            scroller.targetPosition = position
            layoutManager?.startSmoothScroll(scroller)
        } else {
            scrollToPosition(position)
        }
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        // do nothing
    }

    fun setScrollableHeight(height: Int) {
        setHeight(height)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        /**
         * FIXME: 野路子, 需要找到根治方式, 不能每个自定义view都这么处理
         * 修复fragment被回收了之后报的ID重复的异常
         * java.lang.IllegalArgumentException: Wrong state class, expecting View State but received xxx class
         */
        try {
            super.onRestoreInstanceState(state)
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private inner class StartScroller(context: Context?) : LinearSmoothScroller(context) {

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
}