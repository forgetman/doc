package dsb.ext

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import eth.binder.Binder
import eth.model.ErrorDefaultCode
import eth.model.Nive
import sugar.ext.cast
import sugar.ext.self
import vector.app.UIHost
import vector.app.activity.SimpleActivityEx
import vector.app.decor.ViewState
import vector.app.dialog.LoadingDialog
import vector.app.frag.SimpleFragEx
import vector.config.Config
import vector.ext.toast
import vector.ext.view.findAll
import vector.scrollable.widget.ListView
import vector.swiperefresh.widget.LoadMore
import vector.swiperefresh.widget.SwipeRefresh

fun <T> Binder<T>.withLoading(act: FragmentActivity) = self {
    act.cast<UIHost> {
        withAllListState(act, it.hostView)
    }

    withLoading(act, act as Context)
}

fun <T> Binder<T>.withLoading(frag: Fragment) = self {
    frag.cast<UIHost> {
        withAllListState(frag, it.hostView)
    }

    withLoading(frag, frag.context)
}

fun <T> Binder<T>.withLoading(owner: LifecycleOwner, context: Context?) = self {
    if (context == null) return@self

    val dialog = LoadingDialog(context)
    dialog.onDismiss {
        cancel()
    }

    onState(owner) { state ->
        if (state == Binder.State.LOADING) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }
}

fun <T> Binder<T>.withSwipe(owner: LifecycleOwner, delegate: SwipeRefresh.Option) = self {
    onState(owner) {
        if (it != Binder.State.LOADING) delegate.stopRefresh()
    }

    onError(owner) {
        delegate.stopRefresh()
    }
}

fun <T> Binder<T>.withLoadMore(owner: LifecycleOwner, delegate: LoadMore.Option) = self {
    observeComparable(owner) {
        it.second.cast<List<*>> { list ->
            val size = list.size
            if (size < Config.list().limit) {
                delegate.stop(false)
            } else {
                delegate.ready()
            }
        }
    }

    onError(owner) {
        delegate.stop(true)
    }
}

fun <T> Binder<T>.withViewState(act: SimpleActivityEx) = self {
    onState(act) {
        act.viewState = when (it) {
            Binder.State.LOADING -> ViewState.LOADING
            Binder.State.ERROR, Binder.State.CANCEL -> ViewState.ERROR
            Binder.State.SUCCESS -> ViewState.NORMAL
        }
    }

    withAllListState(act, act.hostView)
}

fun <T> Binder<T>.withViewState(frag: SimpleFragEx) = self {
    onState(frag) {
        frag.viewState = when (it) {
            Binder.State.LOADING -> ViewState.LOADING
            Binder.State.ERROR, Binder.State.CANCEL -> ViewState.ERROR
            Binder.State.SUCCESS -> ViewState.NORMAL
        }
    }

    withAllListState(frag, frag.hostView)
}

fun <T> Binder<T>.withToast() = self {
    onError {
        when (it.code) {
            ErrorDefaultCode.NETWORK, ErrorDefaultCode.CONNECT -> toast("网络连接错误, 请检查网络设置")
            else -> toast(it.message)
        }
    }
}

private fun <T> Binder<T>.withAllListState(owner: LifecycleOwner, hostView: View?) {
    val list = hostView?.findAll<ListView>() ?: return
    list.forEach {
        // 为了解决list view加载数据第一页数据少于Page.LIMIT时，分页加载的load没有消失问题
        withLoadMore(owner, it.delegate)
    }
}