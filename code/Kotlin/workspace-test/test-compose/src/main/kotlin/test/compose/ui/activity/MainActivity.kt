package test.compose.ui.activity

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import logger.L
import test.compose.ui.FlowButton
import test.compose.ui.FlowContent
import vector.app.compose.ext.ui.statusBarsTopValue
import vector.app.compose.ui.activity.SimpleComposeActivityEx
import vector.ext.startActivity

class MainActivity : SimpleComposeActivityEx() {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val topValue = WindowInsets.statusBarsTopValue
        L.www("topValue = $topValue")

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Compose首页") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                )
            }
        ) { innerPadding ->
            FlowContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                FlowButton("动画列表") {
                    startActivity<AnimateListActivity>()
                }
                FlowButton("普通列表") {
                    startActivity<SimpleListActivity>()
                }
                FlowButton("下拉刷新列表") {
                    startActivity<PullToRefreshActivity>()
                }
                FlowButton("viewpager") {
                    startActivity<PagerActivity>()
                }
                FlowButton("scaffold") {
                    startActivity<ScaffoldActivity>()
                }
                FlowButton("paging3") {
                    startActivity<PagingActivity>()
                }
                FlowButton("Form") {
                    startActivity<FormActivity>()
                }
                FlowButton("Media") {
                    startActivity<MediaActivity>()
                }
                FlowButton("TTS") {
                    startActivity<TtsActivity>()
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