package test.compose.ui.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import test.compose.ext.AppBar
import test.compose.ui.viewmodel.PagingViewModel
import vector.app.compose.ui.activity.ComposeActivityEx

@AndroidEntryPoint
class PagingActivity : ComposeActivityEx<PagingViewModel>() {

    @Preview
    @Composable
    override fun Content() {
        val lazyPagingItems = viewModel.itemsFlow.collectAsLazyPagingItems()
        val listState: LazyListState = rememberLazyListState()

        Scaffold(
            topBar = {
                AppBar(title = "Paging")
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = listState
            ) {
                items(lazyPagingItems.itemCount) { index ->
                    val item = lazyPagingItems[index]
                    if (item != null) {
                        Text(
                            text = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Color.White
                        )
                    }
                }
                // 处理初始加载状态
                lazyPagingItems.apply {
                    when (val refreshState = loadState.refresh) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Text(
                                    text = "加载错误：${refreshState.error.localizedMessage}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }

                        else -> {} // NotLoading
                    }
                    // 处理分页追加加载状态
                    when (val appendState = loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Button(
                                    onClick = { retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("加载更多失败，点击重试")
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

data class PagingItem(val name: String)

class SamplePagingSource : PagingSource<Int, PagingItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PagingItem> {
        // 当前页号，第一页默认为 1
        val page = params.key ?: 1
        return try {
            // 模拟网络延时
            delay(2000)
            // 模拟每页 20 个数据
            val items = (1..20).map { index ->
                PagingItem("Item ${(page - 1) * 20 + index}")
            }
            // 如果有数据，则下一页为 page+1，否则为 null（结束）
            val nextKey = if (items.isNotEmpty()) page + 1 else null
            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PagingItem>): Int? {
        // 使用锚点附近的页面 key 作为刷新 key
        return state.anchorPosition?.let { anchorPos ->
            state.closestPageToPosition(anchorPos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPos)?.nextKey?.minus(1)
        }
    }
}
