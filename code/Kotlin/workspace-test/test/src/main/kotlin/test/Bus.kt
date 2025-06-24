package test

import bus.flow.FlowBus

class Bus private constructor() : FlowBus() {

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