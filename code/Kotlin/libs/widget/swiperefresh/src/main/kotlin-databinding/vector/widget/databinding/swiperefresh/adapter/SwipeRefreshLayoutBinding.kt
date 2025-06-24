@file:Suppress("unused")

package vector.widget.databinding.swiperefresh.adapter

import android.view.View
import androidx.databinding.BindingAdapter
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshTrigger
import vector.widget.swiperefresh.SwipeRefreshLayout
import vector.widget.swiperefresh.delegate.SwipeRefresh
import vector.widget.swiperefresh.header.BaseSwipeHeader
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout as SwipeRefreshLayoutX

val SwipeRefreshLayoutX.refreshDelegate: SwipeRefresh.Option
    get() = getSwipeRefreshDelegate {
        object : SwipeRefresh.Option {
            override fun startRefresh() {
                this@refreshDelegate.isRefreshing = true
            }

            override fun stopRefresh() {
                this@refreshDelegate.isRefreshing = false
            }
        }
    }

val SwipeRefreshLayout.refreshDelegate: SwipeRefresh.Option
    get() = getSwipeRefreshDelegate {
        object : SwipeRefresh.Option {
            override fun startRefresh() {
                this@refreshDelegate.startRefresh()
            }

            override fun stopRefresh() {
                this@refreshDelegate.stopRefresh()
            }
        }
    }

private fun View.getSwipeRefreshDelegate(initialize: () -> SwipeRefresh.Option): SwipeRefresh.Option {
    val key = vector.app.androidview.R.id.view_on_swipe
    var delegate = getTag(key) as? SwipeRefresh.Option
    if (delegate == null) {
        delegate = initialize.invoke()
        setTag(key, delegate)
    }
    return delegate
}

/**
 * @author yuansui
 * @since 2020/12/28
 */
object SwipeRefreshLayoutBinding {

    private const val ON_SWIPE = BINDING_PREFIX + "refresh_onSwipe"

    private const val SWIPE_HEADER = BINDING_PREFIX + "refresh_header"
    private const val TRIGGER_SWIPE_REFRESH = BINDING_PREFIX + "refresh_trigger"

    @JvmStatic
    @BindingAdapter(ON_SWIPE)
    fun setOnSwipeRefreshX2(view: SwipeRefreshLayoutX, onSwipe: RefreshBind.OnSwipe) {
        view.setOnRefreshListener {
            onSwipe.action(view.refreshDelegate)
        }
    }

    @JvmStatic
    @BindingAdapter(ON_SWIPE)
    fun setOnSwipeRefresh(view: SwipeRefreshLayout, onSwipe: RefreshBind.OnSwipe) {
        view.listener = object : SwipeRefresh.Listener {
            override fun onSwipeStateChanged(state: SwipeRefresh.State) {
                // FIXME: 业务需求上只需要START的状态, 暂时忽略其他状态
                if (state == SwipeRefresh.State.START) onSwipe.action(view.refreshDelegate)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(SWIPE_HEADER)
    fun setSwipeHeader(view: SwipeRefreshLayout, swipeHeader: BaseSwipeHeader) {
        view.swipeHeader = swipeHeader
    }

    @JvmStatic
    @BindingAdapter(TRIGGER_SWIPE_REFRESH)
    fun setTrigger(view: SwipeRefreshLayout, trigger: SwipeRefreshTrigger) {
        trigger.observe { refresh ->
            view.setRefreshing(refresh)
        }
    }

    @JvmStatic
    @BindingAdapter(TRIGGER_SWIPE_REFRESH)
    fun setTriggerX(view: SwipeRefreshLayoutX, trigger: SwipeRefreshTrigger) {
        trigger.observe { refresh ->
            view.isRefreshing = refresh
        }
    }
}