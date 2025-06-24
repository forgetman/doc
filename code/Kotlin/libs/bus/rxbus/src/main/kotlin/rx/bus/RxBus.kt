@file:Suppress("unused")

package rx.bus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import bus.api.AbstractBus
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author yuansui
 * @since 2018/7/20
 */
abstract class RxBus : AbstractBus() {

    private val relay = PublishRelay.create<Event<*>>().toSerialized()

    override fun send(event: Event<*>) {
        relay.accept(event)
    }

    override fun with(owner: LifecycleOwner): BaseObserver {
        return BusObserver(owner)
    }

    override fun forever(): BaseObserver {
        return BusObserver(null)
    }

    override fun release() {
        // have nothing to release
    }

    @Suppress("UNCHECKED_CAST")
    inner class BusObserver internal constructor(owner: LifecycleOwner?) : BaseObserver(owner) {
        private var disposable: Disposable? = null

        private var event: Lifecycle.Event? = null

        override fun <T> onValue(eventId: Int, block: (T) -> Unit) {
            subscribe(eventId) {
                val cast = it.any as? T ?: return@subscribe
                block.invoke(cast)
            }
        }

        override fun <T> onNullableValue(eventId: Int, block: (T?) -> Unit) {
            subscribe(eventId) {
                val cast = it.any as? T?
                block.invoke(cast)
            }
        }

        override fun <R : Event<*>> onEvent(eventId: Int, block: (R) -> Unit) {
            subscribe(eventId) {
                val cast = it as? R ?: return@subscribe
                block.invoke(cast)
            }
        }

        override fun onMessage(eventId: Int, block: () -> Unit) {
            subscribe(eventId) {
                block()
            }
        }

        fun bindUtil(event: Lifecycle.Event): BusObserver {
            this.event = event
            return this
        }

        private fun subscribe(eventId: Int, action: (ev: Event<*>) -> Unit) {
            if (owner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
                return
            }

            owner?.lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    val e = this@BusObserver.event ?: Lifecycle.Event.ON_DESTROY
                    if (e == event) {
                        val d = disposable
                        if (d != null && !d.isDisposed) d.dispose()
                        owner?.lifecycle?.removeObserver(this)
                    }
                }
            })

            relay.filter {
                it.eventId == eventId
            }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete()
                .subscribe(object : Observer<Event<*>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Event<*>) {
                        action(t)
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                })
        }
    }
}
