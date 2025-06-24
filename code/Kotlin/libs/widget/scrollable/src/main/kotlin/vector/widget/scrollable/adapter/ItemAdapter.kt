package vector.widget.scrollable.adapter

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import coroutine.flow.launchForever
import coroutine.flow.stateInForever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.throwIfNull
import vector.app.adapter.Cache
import vector.app.adapter.SparseIntArrayCache
import vector.widget.scrollable.adapter.binder.ItemBinder
import vector.widget.scrollable.delegate.SpanSizeDelegate
import java.lang.ref.WeakReference

fun interface OnItemClickListener {
    fun onItemClick(itemView: View, position: Int)
}

/**
 * 需要使用[DiffUtil]对比的item
 */
interface DiffItem {
    fun areItemsTheSame(other: Any): Boolean
    fun areContentsTheSame(other: Any): Boolean
}

/**
 * 数据对比的方式
 */
enum class ItemCompare {
    DIFF,
    RANGE_CHANGED,
    DATASET_CHANGED
}

/**
 * @author yuansui
 * @since 2021/5/2
 */
open class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>(), SpanSizeDelegate {

    private val binders = mutableListOf<ItemBinder<*, *>>()
    private val onAdapterItemClickListener = OnAdapterItemClickListener()

    /**
     * 保存每个item的viewType
     *
     * PS: 当使用[ItemCompare.DIFF]方式刷新时, 如果比对完后发现没有核心比对内容的改变, 但是又更换了wrapper类的时候
     * 会发生无法clear的问题导致crash, 因为[AdapterListUpdateCallback]没有任何回调
     */
    private val viewTypeCache: Cache = SparseIntArrayCache()

    var itemCompare: ItemCompare = ItemCompare.RANGE_CHANGED

    private val scope: CoroutineScope = MainScope()

    var data: List<Any>? = null
        set(value) {
            val old = field
            field = if (value != null) {
                buildList { addAll(value) }
            } else {
                null
            }
            compareItems(old, value, itemCompare)
        }

    var onItemClickListener: OnItemClickListener? = null
        private set
    var onItemDoubleClickListener: OnItemClickListener? = null
        private set
    var onItemLongClickListener: OnItemClickListener? = null
        private set

    /**
     * 对比新老数据, 决定刷新方式
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun compareItems(old: List<Any>?, new: List<Any>?, compare: ItemCompare) {
        if (old == null && new == null) {
            // 都为null, 无效设置
            notifyDataSetChanged()
            return
        }

        viewTypeCache.clear()

        when (compare) {
            ItemCompare.DIFF -> {
                if (old == null || new == null) {
                    compareItems(old, new, ItemCompare.RANGE_CHANGED)
                    return
                }

                flow {
                    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun getOldListSize(): Int = old.size

                        override fun getNewListSize(): Int = new.size

                        override fun areItemsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem = old.getOrNull(oldItemPosition)
                            val newItem = new.getOrNull(newItemPosition)
                            return if (oldItem is DiffItem && newItem is DiffItem) {
                                oldItem.areItemsTheSame(newItem)
                            } else {
                                oldItem == newItem
                            }
                        }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem = old.getOrNull(oldItemPosition)
                            val newItem = new.getOrNull(newItemPosition)
                            return if (oldItem is DiffItem && newItem is DiffItem) {
                                oldItem.areContentsTheSame(newItem)
                            } else {
                                oldItem == newItem
                            }
                        }

                        override fun getChangePayload(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Any? {
                            val item = new[newItemPosition]
                            return if (!areContentsTheSame(
                                    oldItemPosition,
                                    newItemPosition
                                )
                            ) item else null
                        }
                    })

                    emit(diff)
                }.flowOn(Dispatchers.Default).onEach {
                    it.dispatchUpdatesTo(AdapterListUpdateCallback())
                }.flowOn(Dispatchers.Main).launchIn(scope)
            }

            ItemCompare.RANGE_CHANGED -> {
                when {
                    !old.isNullOrEmpty() && new == null -> {
                        notifyItemRangeRemoved(0, old.size)
                    }

                    old == null && !new.isNullOrEmpty() -> {
                        notifyItemRangeInserted(0, new.size)
                    }

                    old != null && new != null -> {
                        // 对比changed范围
                        val oldSize = old.size
                        val newSize = new.size
                        if (oldSize == newSize) {
                            notifyItemRangeChanged(0, oldSize)
                        } else {
                            notifyDataSetChanged()
                        }
                    }

                    else -> {
                        notifyDataSetChanged()
                    }
                }
            }

            ItemCompare.DATASET_CHANGED -> {
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (binders.isEmpty()) throw IllegalStateException("No ItemBinder registered")
        return if (viewType in 0 until binders.size) {
            binders[viewType].createViewHolder(parent)
        } else {
            binders[0].createViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binder: ItemBinder<*, *> = binders[getItemViewType(position)]
        binder.internalOnBindViewHolder(
            holder,
            getItem(position).throwIfNull("Item not found"),
            position
        )
    }

    override fun getSpanSize(position: Int, spanCount: Int): Int {
        if (itemCount == 0) return super.getSpanSize(position, spanCount)
        return getItemBinder(position).getSpanSize(position, spanCount)
    }

    private fun getItemBinder(adapterPosition: Int): ItemBinder<*, *> {
        return binders[getItemViewType(adapterPosition)]
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return if (hasStableIds()) {
            position.toLong()
        } else {
            super.getItemId(position)
        }
    }

    fun registerItemBinders(itemBinders: List<ItemBinder<*, *>>) {
        binders.addAll(itemBinders)
    }

    open fun getItem(position: Int): Any? {
        return data?.getOrNull(position)
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = viewTypeCache[position, -1]
        if (viewType == -1) {
            val item = getItem(position) ?: return viewType
            viewType = getItemBinderPositionForItem(item)
            viewTypeCache.append(position, viewType)
        }
        return viewType
    }

    private fun getItemBinderPositionForItem(item: Any): Int {
        for ((binderPosition, itemBinder) in binders.withIndex()) {
            if (itemBinder.canBindData(item)) {
                return binderPosition
            }
        }
        throw IllegalStateException("ItemBinder not found for position. Item = $item")
    }

    private inner class AdapterListUpdateCallback : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        onAdapterItemClickListener.attachToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        onAdapterItemClickListener.detachFromRecyclerView(recyclerView)
        scope.cancel()
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
        onAdapterItemClickListener.hasOnItemClickListener.value = listener != null
    }

    fun setOnItemDoubleClickListener(listener: OnItemClickListener?) {
        onItemDoubleClickListener = listener
        onAdapterItemClickListener.hasOnItemDoubleClickListener.value = listener != null
    }

    fun setOnItemLongClickListener(listener: OnItemClickListener?) {
        onItemLongClickListener = listener
        onAdapterItemClickListener.hasOnItemLongClickListener.value = listener != null
    }

    inner class OnAdapterItemClickListener {

        val hasOnItemClickListener = MutableStateFlow(false)
        val hasOnItemDoubleClickListener = MutableStateFlow(false)
        val hasOnItemLongClickListener = MutableStateFlow(false)

        private val hasClickListeners = combine(
            hasOnItemClickListener,
            hasOnItemDoubleClickListener,
            hasOnItemLongClickListener
        ) { hasOnItemClick, hasOnItemDoubleClick, hasOnItemLongClick ->
            detector?.setIsLongpressEnabled(hasOnItemLongClick)
            hasOnItemClick || hasOnItemDoubleClick || hasOnItemLongClick
        }.flowOn(Dispatchers.Main).stateInForever(SharingStarted.WhileSubscribed(), false)
        private var hasJob: Job? = null

        private var recyclerView: RecyclerView? = null

        private var scrollState = RecyclerView.SCROLL_STATE_IDLE
        private val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                scrollState = newState
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    detectorListener.clearPressedState()
                }
            }
        }

        private val detectorListener = object : GestureDetector.SimpleOnGestureListener() {

            private val lastChildViewUnders = mutableListOf<WeakReference<View>>()

            private fun addChildViewUnder(childViewUnder: View) {
                if (lastChildViewUnders.mapNotNull { it.get() }.find { it == childViewUnder } != null) return
                lastChildViewUnders.add(WeakReference(childViewUnder))
            }

            fun clearPressedState() {
                if (lastChildViewUnders.isEmpty()) return
                lastChildViewUnders.mapNotNull { it.get() }.forEach {
                    it.post {
                        // 延迟设置pressed状态, 避免后续的click事件无效
                        it.isPressed = false
                    }
                }
                lastChildViewUnders.clear()
            }

            fun findChildViewUnder(e: MotionEvent): View? {
                return recyclerView?.findChildViewUnder(e.x, e.y)
            }

            private fun onTouchConfirm(e: MotionEvent, listener: OnItemClickListener) {
                val childViewUnder = findChildViewUnder(e)
                if (childViewUnder != null) {
                    val vh = recyclerView?.getChildViewHolder(childViewUnder)
                    if (vh != null) listener.onItemClick(childViewUnder, vh.bindingAdapterPosition)
                    clearPressedState()
                }
            }

            override fun onDown(e: MotionEvent): Boolean {
                if (scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    return false
                }

                val childViewUnder = findChildViewUnder(e)
                /**
                 * 如果有额外的onClickListener, 不能清除pressed状态(click事件响应后会自动清除),
                 * 不然会引起onClickListener后续无效, 既无click事件.
                 * 但是如果不清除, 会导致如果快速点击后,加入click事件没响应(认为不生效), pressed状态会一直保持为true
                 * 最后采取的方式是如果有click事件则不加入clear press的管理
                 */
                if (childViewUnder != null && !childViewUnder.hasClickListeners()) {
                    if (isSdkAtLeast(SdkInt.L_21)) {
                        childViewUnder.drawableHotspotChanged(e.x, e.y)
                    }
                    childViewUnder.isPressed = true
                    addChildViewUnder(childViewUnder)
                    return true
                }
                return false
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return if (onItemDoubleClickListener != null) {
                    // 有双击事件, 不响应
                    super.onSingleTapUp(e)
                } else {
                    onTouchConfirm(e) { itemView, position ->
                        onItemClickListener?.onItemClick(itemView, position)
                    }
                    return onItemClickListener != null
                }
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return if (onItemDoubleClickListener == null) {
                    super.onSingleTapUp(e)
                } else {
                    onTouchConfirm(e) { itemView, position ->
                        onItemClickListener?.onItemClick(itemView, position)
                    }
                    return onItemClickListener != null
                }
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                onTouchConfirm(e) { itemView, position ->
                    onItemDoubleClickListener?.onItemClick(itemView, position)
                }
                return onItemDoubleClickListener != null
            }

            override fun onLongPress(e: MotionEvent) {
                onTouchConfirm(e) { itemView, position ->
                    onItemLongClickListener?.onItemClick(itemView, position)
                }
            }

            private fun View.hasClickListeners(): Boolean {
                return if (isSdkAtLeast(SdkInt.R_30)) {
                    this.hasOnClickListeners() || this.hasOnLongClickListeners()
                } else {
                    this.hasOnClickListeners()
                }
            }
        }

        private var detector: GestureDetector? = null

        private val onItemTouchListener = object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                detector?.onTouchEvent(e)
                cancelPressedState(e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                detector?.onTouchEvent(e)
                cancelPressedState(e)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                // do nothing
            }

            private fun cancelPressedState(e: MotionEvent) {
                if (e.actionMasked == MotionEvent.ACTION_CANCEL) {
                    detectorListener.clearPressedState()
                }
            }
        }

        fun attachToRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = recyclerView
            recyclerView.addOnItemTouchListener(onItemTouchListener)
            recyclerView.addOnScrollListener(onScrollListener)

            hasJob = hasClickListeners.onEach { has ->
                if (has) {
                    detector = GestureDetector(recyclerView.context, detectorListener).apply {
                        setIsLongpressEnabled(onItemLongClickListener != null)
                    }
                }
            }.flowOn(Dispatchers.Main).launchForever()
        }

        fun detachFromRecyclerView(recyclerView: RecyclerView) {
            recyclerView.removeOnItemTouchListener(onItemTouchListener)
            recyclerView.removeOnScrollListener(onScrollListener)
            this.recyclerView = null
            detector = null

            hasJob?.cancel()
            hasJob = null
        }
    }
}

abstract class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)