package eos.api.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coroutine.scope.observeCancel
import eos.api.Eos
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy

fun Eos.onOpen(owner: LifecycleOwner, action: () -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    val listener = object : eos.api.WebSocketListener {
        override fun onOpen() {
            action()
        }
    }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

fun Eos.onOpen(coroutineScope: CoroutineScope, action: () -> Unit) {
    val listener = object : eos.api.WebSocketListener {
        override fun onOpen() {
            action()
        }
    }
    addListener(listener)
    coroutineScope.observeCancel {
        removeListener(listener)
    }
}

@JvmName("onMessageString")
fun Eos.onMessage(owner: LifecycleOwner, action: (String) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    val listener = object : eos.api.WebSocketListener {
        override fun onMessage(message: String) {
            action(message)
        }
    }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

@JvmName("onMessageStringWithScope")
fun Eos.onMessage(coroutineScope: CoroutineScope, action: (String) -> Unit) {
    val listener = object : eos.api.WebSocketListener {
        override fun onMessage(message: String) {
            action(message)
        }
    }
    addListener(listener)
    coroutineScope.observeCancel {
        removeListener(listener)
    }
}

@JvmName("onMessageByteArray")
fun Eos.onMessage(owner: LifecycleOwner, action: (ByteArray) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    val listener = object : eos.api.WebSocketListener {
        override fun onMessage(message: ByteArray) {
            action(message)
        }
    }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

@JvmName("onMessageByteArrayWithScope")
fun Eos.onMessage(coroutineScope: CoroutineScope, action: (ByteArray) -> Unit) {
    val listener = object : eos.api.WebSocketListener {
        override fun onMessage(message: ByteArray) {
            action(message)
        }
    }
    addListener(listener)
    coroutineScope.observeCancel {
        removeListener(listener)
    }
}

fun Eos.onClose(owner: LifecycleOwner, action: (Int, String) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    val listener = object : eos.api.WebSocketListener {
        override fun onClose(code: Int, reason: String) {
            action(code, reason)
        }
    }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

fun Eos.onClose(coroutineScope: CoroutineScope, action: (Int, String) -> Unit) {
    val listener = object : eos.api.WebSocketListener {
        override fun onClose(code: Int, reason: String) {
            action(code, reason)
        }
    }
    addListener(listener)
    coroutineScope.observeCancel {
        removeListener(listener)
    }
}

fun Eos.onFailure(owner: LifecycleOwner, action: (Throwable) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    val listener = object : eos.api.WebSocketListener {
        override fun onFailure(t: Throwable) {
            action(t)
        }
    }
    addListener(listener)
    owner.observeDestroy {
        removeListener(listener)
    }
}

fun Eos.onFailure(coroutineScope: CoroutineScope, action: (Throwable) -> Unit) {
    val listener = object : eos.api.WebSocketListener {
        override fun onFailure(t: Throwable) {
            action(t)
        }
    }
    addListener(listener)
    coroutineScope.observeCancel {
        removeListener(listener)
    }
}

