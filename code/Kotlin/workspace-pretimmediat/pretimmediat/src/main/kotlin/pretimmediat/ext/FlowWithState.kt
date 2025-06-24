package pretimmediat.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import vector.app.activity.SimpleActivityEx
import vector.app.decor.ViewState
import vector.app.frag.SimpleFragEx
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshTrigger
import vector.widget.swiperefresh.delegate.SwipeRefresh

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

/**
 * 优化加载的UI体验, 保证加载时长能展现一个比较完整的加载动画
 */
fun <T> Flow<T>.optimizeLoading(): Flow<T> {
    var startTime = 0L
    return onStart {
        startTime = System.currentTimeMillis()
    }.onEach {
        val diffTime = System.currentTimeMillis() - startTime
        if (diffTime < 1500) {
            // 优化体验. 展示至少1秒的loading
            delay(1500 - diffTime)
        }
    }
}