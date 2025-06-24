package test.compose.ui.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import test.compose.ext.AppBar
import vector.app.compose.ui.activity.SimpleComposeActivityEx

class PullToRefreshActivity : SimpleComposeActivityEx() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // 初始化 item 数量状态
        var itemCount by remember { mutableIntStateOf(15) }
        // 初始化刷新状态
        var isRefreshing by remember { mutableStateOf(false) }
        // 创建下拉刷新状态
        val state = rememberPullToRefreshState()
        // 获取协程作用域
        val coroutineScope = rememberCoroutineScope()
        // 刷新操作的回调函数
        val onRefresh: () -> Unit = {
            isRefreshing = true
            coroutineScope.launch {
                // 模拟耗时操作
                delay(1500)
                // 增加 item 数量
                itemCount += 5
                isRefreshing = false
            }
        }
        Scaffold(
            topBar = {
                AppBar(title = "下拉刷新")
            }
        ) { paddingValues ->
            PullToRefreshBox(
                modifier = Modifier.padding(paddingValues),
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(itemCount) { index ->
                        ListItem(headlineContent = { Text(text = "Item ${itemCount - index}") })
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewLightTheme() {
        MaterialTheme {
            Content()
        }
    }

    @Preview
    @Composable
    fun PreviewDarkTheme() {
        MaterialTheme(colorScheme = darkColorScheme()) {
            Content()
        }
    }
}