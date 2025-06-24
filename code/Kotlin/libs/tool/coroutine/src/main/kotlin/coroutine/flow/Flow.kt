@file:Suppress("OPT_IN_USAGE")

package coroutine.flow

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun <T> T.asFlow() = flow {
    emit(this@asFlow)
}

// <editor-fold defaultstate = expanded" desc = "launchIn">
fun <T> Flow<T>.launchIn(owner: LifecycleOwner) = launchIn(owner.lifecycleScope)
fun <T> Flow<T>.launchIn(lifecycle: Lifecycle) = launchIn(lifecycle.coroutineScope)
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "launchForever">
@OptIn(DelicateCoroutinesApi::class)
fun <T> Flow<T>.launchForever() = launchIn(GlobalScope)
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "launchOnce">
fun <T> Flow<T>.launchOnceIn(owner: LifecycleOwner) = launchOnceIn(owner.lifecycleScope)
fun <T> Flow<T>.launchOnceIn(lifecycle: Lifecycle) = launchOnceIn(lifecycle.coroutineScope)
fun <T> Flow<T>.launchOnceIn(scope: CoroutineScope): Job {
    return scope.launch {
        collect {
            cancel()
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> Flow<T>.launchOnceForever(): Job {
    return GlobalScope.launch {
        collect {
            cancel()
        }
    }
}
// </editor-fold>

fun <T> Flow<T>.stateInForever(
    started: SharingStarted,
    initialValue: T
) = stateIn(GlobalScope, started, initialValue)

fun <T> Flow<T>.shareInForever(
    started: SharingStarted,
    replay: Int = 0
) = shareIn(GlobalScope, started, replay)