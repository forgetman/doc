package vector.app.compose.ui.state.owner

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import vector.app.compose.ui.ViewModelUi
import vector.app.compose.ui.viewmodel.ViewModelEx

interface PagerStateOwner {
    var currentIndex: Int
}

@Suppress("FunctionName")
fun DefaultPagerStateKeeper(): PagerStateOwner {
    return PagerStateOwnerDefaultImpl()
}

private class PagerStateOwnerDefaultImpl : PagerStateOwner {
    override var currentIndex: Int by mutableIntStateOf(0)
}

@Composable
fun <VM> ViewModelUi<VM>.keepPagerState(pageCount: () -> Int): PagerState where VM : ViewModelEx, VM : PagerStateOwner {
    val state = rememberPagerState(initialPage = viewModel.currentIndex, pageCount = pageCount)
    bindPagerState(viewModel, state)
    return state
}

@Composable
fun <T : PagerStateOwner> T.keepPagerState(pageCount: () -> Int): PagerState {
    val state = rememberPagerState(initialPage = currentIndex, pageCount = pageCount)
    bindPagerState(this, state)
    return state
}

@Composable
private fun bindPagerState(
    delegate: PagerStateOwner,
    state: PagerState
) {
    // 监听 currentPage 并同步滚动
    LaunchedEffect(delegate.currentIndex) {
        if (delegate.currentIndex != state.currentPage) {
            state.animateScrollToPage(delegate.currentIndex)
        }
    }

    // 监听 state.currentPage 并同步到 delegate，避免死循环
    LaunchedEffect(state.currentPage) {
        if (delegate.currentIndex != state.currentPage) {
            delegate.currentIndex = state.currentPage
        }
    }
}