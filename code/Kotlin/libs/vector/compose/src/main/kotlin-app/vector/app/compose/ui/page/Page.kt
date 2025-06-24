package vector.app.compose.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vector.app.compose.ui.BuildErrorContent
import vector.app.compose.ui.BuildLoadingContent
import vector.app.compose.ui.BuildStateContent
import vector.app.compose.ui.StateUi
import vector.app.compose.ui.Ui
import vector.app.compose.ui.ViewModelUi
import vector.app.compose.ui.state.ContentState
import vector.app.compose.ui.viewmodel.ViewModelEx
import vector.app.compose.ui.viewmodel.viewModels

interface Page : Ui, StateUi

abstract class StatePage<VM : ViewModelEx> :
    Page,
    ViewModelUi<VM>,
    ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory {

    final override var contentState: ContentState by mutableStateOf(ContentState.NORMAL)

    private var _viewModelStore: ViewModelStore? = null
    override val viewModelStore: ViewModelStore
        get() {
            ensureViewModelStore()
            return _viewModelStore!!
        }

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        ViewModelProvider.NewInstanceFactory()
    }

    override val viewModel: VM by viewModels()

    private var initializeState = false

    @Composable
    override fun InitializeData() {
    }

    @Composable
    final override fun BuildContent() {
        if (!initializeState) {
            initializeState = true

            InitializeData()

            contentState = initialContentState
        }

        val vmContentState by viewModel.contentState.collectAsStateWithLifecycle()
        vmContentState?.takeIf { it != this@StatePage.contentState }?.let {
            this@StatePage.contentState = it
        }

        LaunchedEffect(this.contentState) {
            viewModel.updateContentState(this@StatePage.contentState)
        }

        StateContent()
    }

    @Composable
    override fun StateContent() {
        BuildStateContent()
    }

    @Composable
    override fun LoadingContent() {
        BuildLoadingContent()
    }

    @Composable
    override fun ErrorContent() {
        BuildErrorContent()
    }

    private fun ensureViewModelStore() {
        if (_viewModelStore == null) {
            _viewModelStore = ViewModelStore()
        }
    }
}

abstract class SimplePage : Page {
    final override var contentState: ContentState by mutableStateOf(ContentState.NORMAL)

    private var initializeState = false

    @Composable
    override fun InitializeData() {
    }

    @Composable
    final override fun BuildContent() {
        if (!initializeState) {
            initializeState = true
            InitializeData()
            contentState = initialContentState
        }

        StateContent()
    }

    @Composable
    override fun StateContent() {
        BuildStateContent()
    }

    @Composable
    override fun LoadingContent() {
        BuildLoadingContent()
    }

    @Composable
    override fun ErrorContent() {
        BuildErrorContent()
    }
}