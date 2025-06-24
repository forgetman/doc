package vector.app.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import vector.app.compose.ui.state.ContentState
import vector.app.compose.ui.viewmodel.ViewModelEx
import vector.app.configuration.Configurations

interface Ui {
    @Composable
    fun InitializeData()

    @Composable
    fun BuildContent()

    @Composable
    fun Content()

    fun compositionContext(): CompositionContext? = null
}

interface StateUi {
    val initialContentState: ContentState
        get() = ContentState.NORMAL

    var contentState: ContentState

    @Composable
    fun StateContent()

    @Composable
    fun LoadingContent()

    @Composable
    fun ErrorContent()

    fun retryOnError() {}
}

interface ViewModelUi<VM : ViewModelEx> {
    val viewModel: VM
}

@Composable
internal fun DefaultLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun DefaultErrorContent(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onRetryClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "网络错误, 点击屏幕重试",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun StateUi.BuildLoadingContent() {
    Configurations.ui.loadingContentBuilder?.BuildContent() ?: DefaultLoadingContent()
}

@Composable
internal fun StateUi.BuildErrorContent() {
    Configurations.ui.errorContentBuilder?.BuildContent {
        retryOnError()
    } ?: DefaultErrorContent { retryOnError() }
}

@Composable
internal fun <T> T.BuildStateContent() where T : Ui, T : StateUi {
    when (contentState) {
        ContentState.NORMAL -> Content()
        ContentState.LOADING -> LoadingContent()
        ContentState.ERROR -> ErrorContent()
    }
}