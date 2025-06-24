package reader.ext

import eth.model.ErrorDefaultCode
import eth.model.EthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import logger.L
import sugar.ext.cast
import vector.app.UIHost
import vector.app.activity.SimpleActivityEx
import vector.app.config.Config
import vector.app.decor.ViewState
import vector.app.ext.view.findViewByType
import vector.app.frag.SimpleFragEx
import vector.ext.toast
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshTrigger
import vector.widget.scrollable.ListView
import vector.widget.swiperefresh.delegate.LoadMore
import vector.widget.swiperefresh.delegate.SwipeRefresh
import java.net.ConnectException
import java.net.UnknownHostException

fun <T> Flow<T>.withViewState(host: SimpleActivityEx): Flow<T> {
    return onStart {
        host.viewState = ViewState.LOADING
    }.catch { e ->
        host.viewState = ViewState.ERROR
        throw e
    }.onCompletion { cause ->
        if (cause == null) host.viewState = ViewState.NORMAL
    }.flowOn(Dispatchers.Main)
}

fun <T> Flow<T>.withViewState(host: SimpleFragEx): Flow<T> {
    return onStart {
        host.viewState = ViewState.LOADING
    }.catch { e ->
        host.viewState = ViewState.ERROR
        throw e
    }.onCompletion { cause ->
        if (cause == null) host.viewState = ViewState.NORMAL
    }.flowOn(Dispatchers.Main)
}

fun <T> Flow<T>.withSwipeState(delegate: SwipeRefresh.Option): Flow<T> {
    return onStart {
        delegate.startRefresh()
    }.onCompletion {
        delegate.stopRefresh()
    }.flowOn(Dispatchers.Main)
}

fun <T> Flow<T>.withTriggerState(trigger: SwipeRefreshTrigger): Flow<T> {
    return onStart {
        trigger.trig(true)
    }.onCompletion {
        trigger.trig(false)
    }
}

fun <T> Flow<T>.withToast(): Flow<T> {
    return catch { e ->
        when (e) {
            is ConnectException, is UnknownHostException -> toast(e.message)
            is EthException -> {
                when (e.code) {
                    ErrorDefaultCode.NETWORK, ErrorDefaultCode.CONNECT -> toast("网络连接错误, 请检查网络设置")
                    else -> toast(e.message)
                }
            }

            else -> toast(e.message)
        }
        throw e
    }.flowOn(Dispatchers.Main)
}

fun <T> Flow<T>.withLoadMore(delegate: LoadMore.Option): Flow<T> {
    return onEach {
        it.cast<List<T>> { list ->
            val size = list.size
            if (size < Config.list().limit) {
                delegate.stop(false)
            } else {
                delegate.ready()
            }

        }
    }.catch { e ->
        L.e("withLoadMore", e)
        delegate.stop(true)
        throw e
    }
}

fun <T, R : UIHost> Flow<T>.fixLoadMoreVisibility(host: R?): Flow<T> {
    val hostView = host?.uiView ?: return this
    return fixLoadMoreVisibility(hostView.findViewByType<ListView>())
}

/**
 * 为了解决list view加载数据第一页数据少于Page.LIMIT时，分页加载的load没有消失问题
 */
fun <T> Flow<T>.fixLoadMoreVisibility(listView: ListView?): Flow<T> {
    listView ?: return this
    return withLoadMore(listView.delegate)
}

fun <T> Flow<T>.catch(logTag: String, message: String): Flow<T> {
    return this.catch { e ->
        L.e(logTag, message, e)
    }
}