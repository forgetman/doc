package vector.app.compose.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class LoadingState() {
    var isShowing by mutableStateOf(false)
        private set
    private var onDismiss: (() -> Unit)? = null

    fun show() {
        isShowing = true
    }

    fun onDismissOnce(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }

    fun dismiss() {
        isShowing = false
        onDismiss?.invoke()
        onDismiss = null
    }
}

@Composable
fun rememberLoadingState() = remember { LoadingState() }
