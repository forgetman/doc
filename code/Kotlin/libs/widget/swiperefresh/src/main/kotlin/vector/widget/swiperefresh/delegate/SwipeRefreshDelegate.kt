package vector.widget.swiperefresh.delegate

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshBindTrigger
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshTrigger
import vector.widget.swiperefresh.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout as SwipeRefreshLayoutX

interface SwipeRefreshDelegate {
    val onSwipe: RefreshBind.OnSwipe
    val refreshTrigger: SwipeRefreshTrigger

    fun autoRefresh(lifecycle: Lifecycle, callback: (trigger: SwipeRefreshTrigger) -> Unit)

    fun interface OnSwipeRefreshListener {
        fun onSwipeRefresh(option: SwipeRefresh.Option)
    }

    fun setOnSwipeRefresh(listener: OnSwipeRefreshListener)
}

fun SwipeRefreshDelegate(swipeRefreshLayout: SwipeRefreshLayoutX): SwipeRefreshDelegate {
    return SwipeRefreshXDelegateImpl(swipeRefreshLayout)
}

private class SwipeRefreshXDelegateImpl(
    private val swipeRefreshLayout: SwipeRefreshLayoutX
) : SwipeRefreshDelegate {

    override val onSwipe: RefreshBind.OnSwipe = RefreshBind.OnSwipe {
        onSwipeRefreshListener?.onSwipeRefresh(it)
    }
    override val refreshTrigger: SwipeRefreshTrigger = SwipeRefreshBindTrigger.refresh()

    override fun autoRefresh(lifecycle: Lifecycle, callback: (SwipeRefreshTrigger) -> Unit) {
        flow {
            emit(Unit)
        }.filterNot {
            swipeRefreshLayout.isRefreshing
        }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).onEach {
            callback(refreshTrigger)
        }.flowOn(Dispatchers.Main).launchIn(lifecycle)
    }

    private var onSwipeRefreshListener: SwipeRefreshDelegate.OnSwipeRefreshListener? = null

    override fun setOnSwipeRefresh(listener: SwipeRefreshDelegate.OnSwipeRefreshListener) {
        this.onSwipeRefreshListener = listener
    }
}

fun SwipeRefreshDelegate(swipeRefreshLayout: SwipeRefreshLayout): SwipeRefreshDelegate {
    return SwipeRefreshDelegateImpl(swipeRefreshLayout)
}

private class SwipeRefreshDelegateImpl(
    private val swipeRefreshLayout: SwipeRefreshLayout
) : SwipeRefreshDelegate {

    override val onSwipe: RefreshBind.OnSwipe = RefreshBind.OnSwipe {
        onSwipeRefreshListener?.onSwipeRefresh(it)
    }
    override val refreshTrigger: SwipeRefreshTrigger = SwipeRefreshBindTrigger.refresh()

    override fun autoRefresh(lifecycle: Lifecycle, callback: (SwipeRefreshTrigger) -> Unit) {
        flow {
            emit(Unit)
        }.filterNot {
            swipeRefreshLayout.isRefreshing
        }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).onEach {
            callback(refreshTrigger)
        }.flowOn(Dispatchers.Main).launchIn(lifecycle)
    }

    private var onSwipeRefreshListener: SwipeRefreshDelegate.OnSwipeRefreshListener? = null

    override fun setOnSwipeRefresh(listener: SwipeRefreshDelegate.OnSwipeRefreshListener) {
        this.onSwipeRefreshListener = listener
    }
}