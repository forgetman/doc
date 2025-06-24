@file:Suppress("unused")

package live.bus

import androidx.lifecycle.ExternalLiveData
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import bus.api.BaseBus

/**
 * @author yuansui
 * @since 2018/7/20
 */
abstract class LiveBus : BaseBus() {

    private val liveData = ExternalLiveData<Event<*>>()

    override fun send(event: Event<*>) {
        // 只是用post, 保证send的操作不卡UI线程
        liveData.postValue(event)
    }

    override fun with(owner: LifecycleOwner): BaseObserver {
        return BusObserver(owner)
    }

    override fun release() {
        // have nothing to release
    }

    inner class BusObserver internal constructor(owner: LifecycleOwner) : BaseObserver(owner) {
        private var event: Lifecycle.Event? = null

        override fun <T> onValue(eventId: Int, block: (T) -> Unit) {
            observe {
                if (it.eventId == eventId) {
                    @Suppress("UNCHECKED_CAST")
                    val cast = it.any as? T ?: return@observe
                    block.invoke(cast)
                }
            }
        }

        override fun <T> onNullableValue(eventId: Int, block: (T?) -> Unit) {
            observe {
                if (it.eventId == eventId) {
                    @Suppress("UNCHECKED_CAST")
                    val cast = it.any as? T?
                    block.invoke(cast)
                }
            }
        }

        override fun <R : Event<*>> onEvent(eventId: Int, block: (R) -> Unit) {
            observe {
                if (it.eventId == eventId) {
                    @Suppress("UNCHECKED_CAST")
                    val cast = it as? R ?: return@observe
                    block.invoke(cast)
                }
            }
        }

        override fun onMessage(eventId: Int, block: () -> Unit) {
            observe { if (it.eventId == eventId) block() }
        }

        fun bindUtil(event: Lifecycle.Event): BusObserver {
            this.event = event
            return this
        }

        private fun observe(action: (ev: Event<*>) -> Unit) {
            val o = owner
            if (o == null) {
                liveData.observeForever(action)
            } else {
                liveData.observe(o, Observer(action))
            }
        }
    }
}
