package fund

import rx.bus.RxBus

object EventId {
    const val LOGIN = 1
    const val LOGOUT = 2
    const val REFRESH_HOME = 3
}

/**
 * @author yuansui
 * @since 2018/8/11 0011
 */
class Bus private constructor() : RxBus() {

    companion object {
        private var bus: Bus? = null
            get() {
                if (field == null) field = Bus()
                return field
            }

        fun get(): Bus {
            return bus ?: Bus()
        }

        fun close() {
            bus?.release()
            bus = null
        }
    }
}