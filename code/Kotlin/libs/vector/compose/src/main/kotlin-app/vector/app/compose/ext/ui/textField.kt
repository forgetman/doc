package vector.app.compose.ext.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * 点击空白区域收起键盘
 */
@Composable
fun Modifier.hideKeyboardOnTouchOutside(): Modifier {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    return this.pointerInput(Unit) { // Use pointerInput directly
        detectTapGestures(onTap = {
            keyboardController?.hide()
            focusManager.clearFocus()
        })
    }
}