@file:OptIn(ExperimentalMaterial3Api::class)

package test.compose.ui.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import test.compose.ui.viewmodel.ScaffoldViewModel
import vector.app.compose.ui.activity.ComposeActivityEx
import vector.app.compose.ui.viewmodel.NavigateTarget
import vector.ext.startActivity

class ScaffoldActivity : ComposeActivityEx<ScaffoldViewModel>() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // 用于记录底部导航栏中当前选中项的索引
        var selectedItem by remember { mutableIntStateOf(0) }
        // 底部导航栏的菜单项
        val items = listOf("Home", "Settings")

        Scaffold(
            topBar = {
                // Material 3 中的顶栏组件
                CenterAlignedTopAppBar(
                    title = { Text("Full Scaffold Example") },
                    navigationIcon = {
                        IconButton(onClick = {
                            // 返回事件处理
                            finish()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                // Material 3 中的底部导航栏容器
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        // Material 3 中的底部导航栏项
                        NavigationBarItem(
                            icon = {
                                // 根据索引设置对应的图标
                                val icon = when (index) {
                                    0 -> Icons.Filled.Home
                                    1 -> Icons.Filled.Settings
                                    else -> Icons.Filled.Home
                                }
                                Icon(icon, contentDescription = null)
                            },
                            label = { Text(item) },
                            // 判断当前项是否被选中
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            },
            floatingActionButton = {
                // Material 3 中的浮动操作按钮
                ExtendedFloatingActionButton(
                    onClick = { /* 点击事件处理 */ },
                    text = { Text("Add") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) }
                )
            },
            content = { padding ->
                // 根据选中项的索引显示不同的内容
                when (selectedItem) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Home Content",
                            textAlign = TextAlign.Center
                        )
                    }

                    1 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Settings Content",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        )
    }

    override fun onNavigate(target: NavigateTarget) {
        when (target.route) {
            "paging" -> startActivity<PagingActivity>()
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