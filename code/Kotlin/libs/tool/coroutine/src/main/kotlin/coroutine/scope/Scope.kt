package coroutine.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

fun CoroutineScope.observeCancel(onCancel: () -> Unit): Job {
    return launch {
        suspendCancellableCoroutine { co ->
            co.invokeOnCancellation {
                onCancel()
            }
        }
    }
}
