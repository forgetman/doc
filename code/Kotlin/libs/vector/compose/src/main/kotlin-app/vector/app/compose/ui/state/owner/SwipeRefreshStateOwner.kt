package vector.app.compose.ui.state.owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import vector.app.compose.ui.viewmodel.ViewModelEx

interface SwipeRefreshStateOwner {
    var isSwipeRefreshing: Boolean

    var isSwipeRefreshEnabled: Boolean

    @OptIn(ExperimentalMaterial3Api::class)
    val pullToRefreshState: PullToRefreshState

    fun onRefresh()
    fun setOnRefresh(callback: Callback)

    fun interface Callback {
        fun onRefresh()
    }
}

@Suppress("FunctionName")
fun DefaultSwipeRefreshStateOwner(): SwipeRefreshStateOwner {
    return SwipeRefreshStateOwnerDefaultImpl()
}

private class SwipeRefreshStateOwnerDefaultImpl() : SwipeRefreshStateOwner {
    private var refreshCallback: SwipeRefreshStateOwner.Callback? = null

    override var isSwipeRefreshing: Boolean by mutableStateOf(false)
    override var isSwipeRefreshEnabled: Boolean by mutableStateOf(true)

    @OptIn(ExperimentalMaterial3Api::class)
    override val pullToRefreshState: PullToRefreshState = PullToRefreshState()

    override fun onRefresh() {
        refreshCallback?.onRefresh()
    }

    override fun setOnRefresh(callback: SwipeRefreshStateOwner.Callback) {
        refreshCallback = callback
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <VM> SwipeRefreshBox(
    viewModel: VM,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) where VM : ViewModelEx, VM : SwipeRefreshStateOwner {
    PullToRefreshBox(
        state = viewModel.pullToRefreshState,
        isRefreshing = viewModel.isSwipeRefreshing,
        onRefresh = { viewModel.onRefresh() },
        modifier = modifier,
        enabled = viewModel.isSwipeRefreshEnabled,
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * 不直接使用[androidx.compose.material3.pulltorefresh.PullToRefreshBox]的原因是没有开放enabled的设置
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier.pullToRefresh(state = state, enabled = enabled, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}