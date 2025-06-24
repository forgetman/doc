package test.compose.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import logger.L
import test.compose.ext.AppBar
import vector.app.compose.ui.activity.SimpleComposeActivityEx
import vector.app.compose.ui.page.SimplePage
import vector.app.compose.ui.page.StatePage
import vector.app.compose.ui.viewmodel.ViewModelEx

class PagerActivity : SimpleComposeActivityEx() {

    @Preview
    @Composable
    override fun Content() {
        val state = rememberPagerState { 3 }

        Scaffold(
            topBar = {
                AppBar(title = "Pager")
            }
        ) { innerPadding ->
            HorizontalPager(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) { page ->
                if (page == 0) {
                    Page1(page).BuildContent()
                } else {
                    Page2(page).BuildContent()
                }
            }
        }
    }
}

class Page1(private val index: Int) : SimplePage() {

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Blue)
                .fillMaxSize()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "page$index, 简单页", fontSize = 32.sp)
        }
    }
}

class Page2ViewModel() : ViewModelEx() {

    init {
        L.www("page2 viewmodel init")
    }
}

class Page2(private val index: Int) : StatePage<Page2ViewModel>() {

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Blue)
                .fillMaxSize()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "page$index, viewmodel", fontSize = 32.sp)
        }
    }
}