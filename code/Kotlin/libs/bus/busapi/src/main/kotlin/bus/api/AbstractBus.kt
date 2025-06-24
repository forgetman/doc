package bus.api

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope

/**
 * @author yuansui
 * @since 2020/11/13
 */
abstract class AbstractBus {

    abstract fun send(event: Event<*>)

    fun send(id: Int, any: Any? = null) {
        val event = DefaultEvent(id)
        event.any = any
        send(event)
    }

    fun with(owner: LifecycleOwner): Observer = createObserver(owner)
    fun with(scope: CoroutineScope): Observer = createObserver(scope)
    fun with(count: Int): Observer = createObserver(count)
    fun forever(): Observer = createObserver()

    protected abstract fun createObserver(owner: LifecycleOwner): Observer
    protected abstract fun createObserver(scope: CoroutineScope): Observer
    protected abstract fun createObserver(): Observer
    protected abstract fun createObserver(count: Int): Observer

    abstract fun release()

    interface Observer {
        fun <T> onValue(eventId: Int, action: (T) -> Unit)
        fun <T> onStickyValue(eventId: Int, action: (T) -> Unit)

        fun <T> onValueOrNull(eventId: Int, action: (T?) -> Unit)
        fun <T> onStickyValueOrNull(eventId: Int, action: (T?) -> Unit)

        fun <R : Event<*>> onEvent(eventId: Int, action: (R) -> Unit)
        fun <R : Event<*>> onStickyEvent(eventId: Int, action: (R) -> Unit)

        /**
         * 只监听事件消息不需要其他参数
         */
        fun onMessage(eventId: Int, action: () -> Unit)
        fun onStickyMessage(eventId: Int, action: () -> Unit)
    }
}

abstract class Event<T>(val eventId: Int) {
    var any: T? = null
    val timestamp: Long = System.currentTimeMillis()
}

internal class DefaultEvent(eventId: Int) : Event<Any>(eventId)