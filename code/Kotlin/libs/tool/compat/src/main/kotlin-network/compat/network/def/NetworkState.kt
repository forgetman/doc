package compat.network.def

/**
 * 网络状态
 */
sealed class NetworkState(val validated: Boolean) {
    object Idle : NetworkState(false)
    class Cellular(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Cellular"
        }
    }

    class Wifi(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Wifi"
        }
    }

    class Ethernet(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Ethernet"
        }
    }

    class Bluetooth(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Bluetooth"
        }
    }

    class Vpn(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Vpn"
        }
    }

    class WifiAware(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "WifiAware"
        }
    }

    class Lowpan(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Lowpan"
        }
    }

    class Usb(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Usb"
        }
    }

    class Thread(validated: Boolean) : NetworkState(validated) {
        companion object {
            fun tag() = "Thread"
        }
    }

    override fun toString(): String {
        return "NetworkState.${this.javaClass.simpleName}(validated = $validated)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as NetworkState
        return validated == other.validated
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun tag() = this.javaClass.simpleName
}