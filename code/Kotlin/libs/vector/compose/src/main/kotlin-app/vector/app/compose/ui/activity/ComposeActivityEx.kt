package vector.app.compose.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vector.app.compose.ui.ViewModelUi
import vector.app.compose.ui.viewmodel.NavigateTarget
import vector.app.compose.ui.viewmodel.ViewModelEx
import vector.app.compose.ui.viewmodel.initViewTreeOwners
import vector.app.compose.ui.viewmodel.viewModels
import vector.app.configuration.Configurations

abstract class ComposeActivityEx<VM : ViewModelEx> : SimpleComposeActivityEx(), ViewModelUi<VM> {

    override val viewModel: VM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewTreeOwners()

        viewModel.navigateEvent
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { target ->
                when (target) {
                    is NavigateTarget.Back -> {
                        val params = target.params
                        if (params != null) {
                            setResult(RESULT_OK, Intent().apply {
                                putExtras(params)
                            })
                        }
                        finish()
                    }
                    else -> {
                        onNavigate(target)
                    }
                }
            }.launchIn(this)
    }

    @Composable
    final override fun BuildContent() {
        Configurations.ui.Theme {
            InitializeData()

            contentState = initialContentState

            val vmContentState by viewModel.contentState.collectAsStateWithLifecycle()
            vmContentState?.takeIf { it != this.contentState }?.let {
                this.contentState = it
            }

            LaunchedEffect(this.contentState) {
                viewModel.updateContentState(this@ComposeActivityEx.contentState)
            }

            StateContent()
        }
    }

    open fun onNavigate(target: NavigateTarget) {
    }
}