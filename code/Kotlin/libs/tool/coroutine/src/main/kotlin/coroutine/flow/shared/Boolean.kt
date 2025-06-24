package coroutine.flow.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


fun MutableSharedFlow<Boolean>.toTrue(
    scope: CoroutineScope? = null,
    context: CoroutineContext = Dispatchers.Default
) {
    scope?.launch(context) {
        emit(true)
    } ?: tryEmit(true)
}

fun MutableSharedFlow<Boolean>.toFalse(
    scope: CoroutineScope? = null,
    context: CoroutineContext = Dispatchers.Default
) {
    scope?.launch(context) {
        emit(false)
    } ?: tryEmit(false)
}