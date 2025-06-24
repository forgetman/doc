@file:Suppress("EXPERIMENTAL_API_USAGE")

package bus.flow

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import bus.api.AbstractBus
import bus.api.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.launchIn as ktxLaunchIn

/**
 * @author yuansui
 * @since 2020-08-15
 */
@Suppress("UNCHECKED_CAST")
abstract class FlowBus : AbstractBus() {

    // 使用 Map 存储每个 eventId 对应的 Flow
    private val flowMap = mutableMapOf<Int, MutableSharedFlow<Event<*>>>()
    private val superScope: CoroutineScope = MainScope()

    private fun getFlow(eventId: Int): MutableSharedFlow<Event<*>> {
        return flowMap.getOrPut(eventId) {
            MutableSharedFlow(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    final override fun send(event: Event<*>) {
        val flow = getFlow(event.eventId)
        flow.tryEmit(event) // 发送事件
    }

    final override fun createObserver(owner: LifecycleOwner): Observer {
        return ObserverImpl(owner.lifecycleScope)
    }

    final override fun createObserver(scope: CoroutineScope): Observer {
        return ObserverImpl(scope)
    }

    final override fun createObserver(): Observer {
        return ObserverImpl(null)
    }

    final override fun createObserver(count: Int): Observer {
        return ObserverImpl(null, count)
    }

    private inner class ObserverImpl(
        private val scope: CoroutineScope?,
        private val takeCount: Int = -1
    ) : Observer {

        override fun <T> onValue(eventId: Int, action: (T) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .withoutReplay()
                .matchEventId(eventId)
                .onValue(action)
                .launchIn(scope)
        }

        override fun <T> onStickyValue(eventId: Int, action: (T) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .matchEventId(eventId)
                .onValue(action)
                .launchIn(scope)
        }

        override fun <T> onValueOrNull(eventId: Int, action: (T?) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .withoutReplay()
                .matchEventId(eventId)
                .onValueOrNull(action)
                .launchIn(scope)
        }

        override fun <T> onStickyValueOrNull(eventId: Int, action: (T?) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .matchEventId(eventId)
                .onValueOrNull(action)
                .launchIn(scope)
        }

        override fun <R : Event<*>> onEvent(eventId: Int, action: (R) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .withoutReplay()
                .matchEventId(eventId)
                .onEvent(action)
                .launchIn(scope)
        }

        override fun <R : Event<*>> onStickyEvent(eventId: Int, action: (R) -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .matchEventId(eventId)
                .onEvent(action)
                .launchIn(scope)
        }

        override fun onMessage(eventId: Int, action: () -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .withoutReplay()
                .matchEventId(eventId)
                .onEach { action() }
                .launchIn(scope)
        }

        override fun onStickyMessage(eventId: Int, action: () -> Unit) {
            getFlow(eventId).markTime()
                .takeCount(takeCount)
                .matchEventId(eventId)
                .onEach { action() }
                .launchIn(scope)
        }
    }

    private fun <T> Flow<T>.takeCount(count: Int): Flow<T> {
        return if (count > 0) take(count) else this
    }

    private fun <T> Flow<T>.markTime(): Flow<Pair<T, Long>> {
        val subscribeTime = System.currentTimeMillis()
        return map { it to subscribeTime }
    }

    private fun <T : Event<*>> Flow<Pair<T, Long>>.withoutReplay(): Flow<Pair<T, Long>> {
        return this.filter { (event, subscribeTime) ->
            event.timestamp >= subscribeTime
        }
    }

    private fun <T : Event<*>> Flow<Pair<T, Long>>.matchEventId(eventId: Int): Flow<Pair<T, Long>> {
        return this.filter { (e, _) ->
            e.eventId == eventId
        }
    }

    private fun <T : Event<*>, R> Flow<Pair<T, Long>>.onValue(action: (R) -> Unit): Flow<Pair<T, Long>> {
        return this.onEach { (e, _) ->
            val value: R = e.castAny<R>() ?: return@onEach
            action(value)
        }
    }

    private fun <T : Event<*>, R> Flow<Pair<T, Long>>.onValueOrNull(action: (R?) -> Unit): Flow<Pair<T, Long>> {
        return this.onEach { (e, _) ->
            val value = e.castAny<R>()
            action(value)
        }
    }

    private fun <T : Event<*>, R> Flow<Pair<T, Long>>.onEvent(action: (R) -> Unit): Flow<Pair<T, Long>> {
        return this.onEach { (e, _) ->
            val value = e as? R? ?: return@onEach
            action(value)
        }
    }

    private fun <T> Flow<T>.launchIn(scope: CoroutineScope?) {
        ktxLaunchIn(scope ?: superScope)
    }

    private fun <T> Event<*>.castAny() = any as? T?

    override fun release() {
        superScope.cancel()
        flowMap.clear()
    }
}
