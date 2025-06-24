package vector.widget.scrollable.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vector.app.ext.view.findFirstVisibleItemPosition
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.widget.scrollable.delegate.SpanSizeDelegate
import vector.widget.swiperefresh.SwipeRefreshConfig
import vector.widget.swiperefresh.delegate.LoadMore
import vector.widget.swiperefresh.footer.BaseFooter
import vector.widget.swiperefresh.footer.DefaultFooter

/**
 * @author yuansui
 * @since 2021/4/27
 */
class LoadMoreAdapter() : RecyclerView.Adapter<LoadMoreAdapter.ViewHolder>(),
    LoadMore.Option, SpanSizeDelegate {

    companion object {
        private const val PRELOAD_RATE: Float = 0.8f
    }

    /**
     * 数据预加载
     */
    var preLoad: Boolean = false
    private var preLoadStarted: Boolean = false

    private var state: LoadMore.State = LoadMore.State.DETACH
        set(value) {
            if (field == value) {
                return
            }

            when {
                field == LoadMore.State.DETACH -> {
                    notifyItemInserted(0)
                }

                value == LoadMore.State.DETACH -> {
                    notifyItemRemoved(0)
                }

                else -> {
                    notifyItemChanged(0)
                }
            }

            field = value
        }

    private var listener: LoadMore.Listener? = null

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE
                || state != LoadMore.State.READY
            ) return

            val layoutManager = rv.layoutManager ?: return
            val visibleItemCount = layoutManager.childCount
            val total = visibleItemCount + layoutManager.findFirstVisibleItemPosition()
            if (total >= layoutManager.itemCount) {
                startLoading()
            }
        }

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            if (!preLoad
                || state != LoadMore.State.READY
                || preLoadStarted
            ) return

            val layoutManager = rv.layoutManager ?: return
            val visibleItemCount = layoutManager.childCount
            val total = visibleItemCount + layoutManager.findFirstVisibleItemPosition()
            val itemCount = layoutManager.itemCount
            val triggerCount = itemCount * PRELOAD_RATE
            if (total >= triggerCount) {
                preLoadStarted = true
                startLoading()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    override fun ready() {
        state = LoadMore.State.READY
    }

    override fun stop(hasError: Boolean) {
        state = if (hasError) {
            LoadMore.State.ERROR
        } else {
            LoadMore.State.DETACH
        }
    }

    override fun setListener(listener: LoadMore.Listener) {
        this.listener = listener
    }

    override fun getSpanSize(position: Int, spanCount: Int): Int {
        return spanCount
    }

    private fun startLoading() {
        listener?.onLoading(state)
        state = LoadMore.State.LOADING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SwipeRefreshConfig.footerConstructor?.invoke(parent.context)
            ?: DefaultFooter(parent.context)
        if (view.layoutParams == null) {
            view.layoutParams = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)
        }
        view.onRetryClick = { startLoading() }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val footer = holder.itemView as? BaseFooter ?: return
        footer.changeState(state)
    }

    override fun getItemCount(): Int {
        return if (state == LoadMore.State.DETACH) 0 else 1
    }

    class ViewHolder(itemView: BaseFooter) : RecyclerView.ViewHolder(itemView)

    val dataObserver by lazy(LazyThreadSafetyMode.NONE) {
        object : RecyclerView.AdapterDataObserver() {

            override fun onChanged() {
                if (preLoad) preLoadStarted = false
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (preLoad) preLoadStarted = false
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                if (preLoad) preLoadStarted = false
            }
        }
    }
}