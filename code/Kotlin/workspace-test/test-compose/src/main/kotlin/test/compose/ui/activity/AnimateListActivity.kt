package test.compose.ui.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import test.compose.ui.ListItem
import test.compose.ext.AppBar
import vector.app.compose.ui.activity.SimpleComposeActivityEx
import vector.app.compose.ui.foundation.animateItemsIndexed

class AnimateListActivity : SimpleComposeActivityEx() {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val items = remember {
            mutableStateListOf(
                Item(1, "Item 1"),
                Item(2, "Item 2"),
                Item(3, "Item 3")
            )
        }
        var nextId by remember { mutableStateOf(4) }

        Scaffold(
            topBar = {
                AppBar(title = "动画列表")
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Button(onClick = {
                        items.add(0, Item(nextId, "New Item $nextId"))
                        nextId++
                    }) {
                        Text("添加项")
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Button(onClick = {
                        if (items.isNotEmpty()) items.removeAt(0)
                    }) {
                        Text("删除项")
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Button(onClick = {
                        if (items.size > 1) {
                            val movedItem = items.removeAt(0)
                            items.add(1, movedItem) // 将第一项移动到第二项
                        }
                    }) {
                        Text("移动第一项")
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Button(onClick = {
                        if (items.isNotEmpty()) {
                            val index = (items.indices).random()
                            items[index] = items[index].copy(name = "Updated ${System.currentTimeMillis()}")
                        }
                    }) {
                        Text("更新随机项")
                    }
                    Button(onClick = {
                        if (items.isNotEmpty()) {
                            items.shuffle()
                        }
                    }) {
                        Text("打乱")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    animateItemsIndexed(
                        items = items,
                        key = { index, item -> item.id }
                    ) { _, item ->
                        ListItem(item)
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

    data class Item(val id: Int, var name: String)
}
