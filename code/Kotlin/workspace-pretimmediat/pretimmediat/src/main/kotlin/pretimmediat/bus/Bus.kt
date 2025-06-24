package pretimmediat.bus

import androidx.lifecycle.LifecycleOwner
import bus.api.Event
import bus.flow.FlowBus
import kotlinx.coroutines.CoroutineScope
import vector.singleton.Singleton2

class Bus private constructor() : FlowBus() {
    companion object : Singleton2<Bus> by Singleton2({
        Bus()
    })
}


fun sendMessage(eventId: Int) {
    Bus.getInstance().send(eventId)
}

fun sendMessage(event: Event<Any>) {
    Bus.getInstance().send(event)
}

fun sendMessage(id: Int, any: Any? = null) {
    Bus.getInstance().send(id, any)
}

fun CoroutineScope.withBus() = Bus.getInstance().with(this)
fun LifecycleOwner.withBus() = Bus.getInstance().with(this)