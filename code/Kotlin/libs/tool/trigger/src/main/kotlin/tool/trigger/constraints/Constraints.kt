package tool.trigger.constraints

import sugar.ext.self

enum class NetworkType {
    NOT_REQUIRED,
    CONNECTED, // 只要连了网, 不管网络是否有效(能否访问外网)
    VALIDATED, // 网络有效(能访问外网)
}

class Constraints private constructor(
    val requiredNetworkType: NetworkType = NetworkType.NOT_REQUIRED,
    val requiresCharging: Boolean = false,
    val requiresTemperatureNotHigh: Boolean = false,
    val requiresScreenOn: Boolean = false,
    val requiresScreenOff: Boolean = false,
    val requiresPowerDownRateNotHigh: Boolean = false,
) {

    companion object {
        @JvmField
        val NONE = Constraints()
    }

    internal var tag: String? = null

    class Builder {
        private var requiredNetworkType = NetworkType.NOT_REQUIRED
        private var requiresCharging = false
        private var requiresTemperatureNotHigh = false
        private var requiresScreenOn = false
        private var requiresScreenOff = false
        private var requiresPowerDownRateNotHigh = false

        fun setRequiredNetworkType(networkType: NetworkType) = self {
            requiredNetworkType = networkType
        }

        fun setRequiresCharging(requiresCharging: Boolean) = self {
            this.requiresCharging = requiresCharging
        }

        fun setRequiresTemperatureNotHigh(requires: Boolean) = self {
            this.requiresTemperatureNotHigh = requires
        }

        fun setRequiresScreenOn(requires: Boolean) = self {
            this.requiresScreenOn = requires
        }

        fun setRequiresScreenOff(requires: Boolean) = self {
            this.requiresScreenOff = requires
        }

        fun setRequiresPowerDownRateNotHigh(requires: Boolean) = self {
            this.requiresPowerDownRateNotHigh = requires
        }

        fun build(): Constraints {
            return Constraints(
                requiredNetworkType,
                requiresCharging,
                requiresTemperatureNotHigh,
                requiresScreenOn,
                requiresScreenOff,
                requiresPowerDownRateNotHigh
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as Constraints

        if (requiredNetworkType != that.requiredNetworkType) return false
        if (requiresCharging != that.requiresCharging) return false
        if (requiresTemperatureNotHigh != that.requiresTemperatureNotHigh) return false
        if (requiresScreenOn != that.requiresScreenOn) return false
        if (requiresScreenOff != that.requiresScreenOff) return false
        if (requiresPowerDownRateNotHigh != that.requiresPowerDownRateNotHigh) return false

        return true
    }


    override fun toString(): String {
        return buildString {
            append("Constraints(")
            if (!tag.isNullOrEmpty()) append("tag = $tag, ")
            if (requiredNetworkType != NetworkType.NOT_REQUIRED) append("requiredNetworkType = $requiredNetworkType, ")
            if (requiresCharging) append("requiresCharging, ")
            if (requiresTemperatureNotHigh) append("requiresTemperatureNotHigh, ")
            if (requiresScreenOn) append("requiresScreenOn, ")
            if (requiresScreenOff) append("requiresScreenOff, ")
            if (requiresPowerDownRateNotHigh) append("requiresPowerDownRateNotHigh, ")
            append(")")
        }
    }

    override fun hashCode(): Int {
        var result = requiredNetworkType.hashCode()
        result = 31 * result + requiresCharging.hashCode()
        result = 31 * result + requiresTemperatureNotHigh.hashCode()
        result = 31 * result + requiresScreenOn.hashCode()
        result = 31 * result + requiresScreenOff.hashCode()
        result = 31 * result + requiresPowerDownRateNotHigh.hashCode()
        return result
    }
}