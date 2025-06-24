package vector.app.compose.ui.activity

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import vector.app.compose.ext.ui.ProvideCompositionLocals
import vector.app.compose.ui.BuildErrorContent
import vector.app.compose.ui.BuildLoadingContent
import vector.app.compose.ui.BuildStateContent
import vector.app.compose.ui.StateUi
import vector.app.compose.ui.Ui
import vector.app.compose.ui.state.ContentState
import vector.app.configuration.Configurations

abstract class SimpleComposeActivityEx : ComponentActivity(), Ui, StateUi {

    final override var contentState: ContentState by mutableStateOf(initialContentState)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFormat(PixelFormat.TRANSPARENT)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent(parent = compositionContext()) {
//            enableEdgeToEdge()
            ProvideCompositionLocals {
                BuildContent()
            }
        }
    }

    @Composable
    override fun InitializeData() {
    }

    @Composable
    override fun BuildContent() {
        Configurations.ui.Theme {
            InitializeData()
            StateContent()
        }
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