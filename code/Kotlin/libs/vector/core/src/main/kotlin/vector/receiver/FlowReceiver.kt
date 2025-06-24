package vector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.ContextCompat
import compat.context.def.ReceiverFlags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 使用 [MutableSharedFlow] 实现的 [BroadcastReceiver]
 */
abstract class FlowReceiver<T> : BroadcastReceiver() {

    abstract val filter: IntentFilter

    private val _messageFlow: MutableSharedFlow<T> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    internal val messageFlow = _messageFlow.asSharedFlow()

    protected fun tryEmit(data: T) {
        _messageFlow.tryEmit(data)
    }

    abstract override fun onReceive(context: Context, intent: Intent)
}

fun <T, R> T.asFlow(
    context: Context,
    flags: ReceiverFlags = ReceiverFlags.RECEIVER_NOT_EXPORTED,
    initialize: suspend ProducerScope<R>.(T) -> Unit = {}
) where T : FlowReceiver<R> = callbackFlow {
    // 先注册广播
    ContextCompat.registerReceiver(context, this@asFlow, filter, flags)

    initialize(this@asFlow)
    messageFlow.onEach { trySend(it) }.flowOn(Dispatchers.IO).launchIn(this)

    awaitClose {
        context.unregisterReceiver(this@asFlow)
    }
}