package fund.ext

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import eth.Nive
import eth.ObservableExecutor
import vector.design.ui.activity.BaseDBActivityEx
import vector.design.ui.decor.ViewState
import vector.design.ui.dialog.LoadingDialog
import vector.design.ui.frag.SimpleFragEx
import vector.design.ui.plugin.LoadMore
import vector.ext.toast
import vector.listConfig
import vector.view.scrollable.ListView
import vector.view.scrollable.ScrollableView

fun <T> ObservableExecutor<T>.withLoading(context: Context) {
    val dialog = LoadingDialog(context)
    dialog.onDismiss {
        cancel()
    }

    observeState { state ->
        if (state == Binder.State.LOADING) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }

    observeError {
        context.toast(it.message)
    }
}

fun <T> ObservableExecutor<T>.withListState(owner: LifecycleOwner, view: ScrollableView<*>) {
    observe(owner) {
        if (it !is List<*>) return@observe
        if (it.size < listConfig.limit) {
            view.stopLoadMore(LoadMore.State.END)
        } else {
            view.resetLoadMoreState()
        }
    }

    observeState(owner) {
        if (it != Binder.State.LOADING) view.stopSwipeRefresh()
    }

    observeError(owner) {
        view.stopSwipeRefresh()
        view.stopLoadMore(LoadMore.State.END)
    }
}

fun <T> ObservableExecutor<T>.withViewState(act: BaseDBActivityEx) {
    observeState(act) {
        act.viewState = when (it) {
            Binder.State.LOADING -> ViewState.LOADING
            Binder.State.ERROR -> ViewState.NORMAL
            Binder.State.SUCCESS -> ViewState.NORMAL
        }
    }

    observeError(act) {
        act.toast(it.message)
    }

    val listView = getListView(act.decorView.contentView)
    listView?.let {
        // 为了解决list view加载数据第一页数据少于Page.LIMIT时，分页加载的load没有消失问题
        withListState(act, it)
    }
}

fun <T> ObservableExecutor<T>.withViewState(frag: SimpleFragEx) {
    observeState(frag) {
        frag.viewState = when (it) {
            Binder.State.LOADING -> ViewState.LOADING
            Binder.State.ERROR -> ViewState.NORMAL
            Binder.State.SUCCESS -> ViewState.NORMAL
        }
    }

    observeError(frag) {
        frag.toast(it.message)
    }

    val listView = getListView(frag.decorView.contentView)
    listView?.let {
        // 为了解决list view加载数据第一页数据少于Page.LIMIT时，分页加载的load没有消失问题
        withListState(frag, it)
    }
}

/**
 * 通过递归寻找ListView
 */
private fun getListView(view: View): ListView? {
    if (view is ListView) {
        return view
    } else {
        if (view is ViewGroup) {
            val childCount = view.childCount
            for (i in 0..childCount) {
                return getListView(view.getChildAt(i))
            }
        }
        return null
    }
}