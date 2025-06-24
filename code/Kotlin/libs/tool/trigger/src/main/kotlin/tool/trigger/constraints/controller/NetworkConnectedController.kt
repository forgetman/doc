package tool.trigger.constraints.controller

import compat.network.def.NetworkState
import tool.trigger.TriggerSpec
import tool.trigger.constraints.Constraints
import tool.trigger.constraints.NetworkType
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 网络状态
 */
internal class NetworkConnectedController(
    tracker: ConstraintTracker<NetworkState>
) : ConstraintController<NetworkState>(tracker) {

    private var constraints: Constraints? = null

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        val has = spec.constraints.requiredNetworkType == NetworkType.CONNECTED
                || spec.constraints.requiredNetworkType == NetworkType.VALIDATED
        if (has) this.constraints = spec.constraints
        return has
    }

    override fun isConstrained(value: NetworkState): Boolean {
        return when (constraints?.requiredNetworkType) {
            NetworkType.VALIDATED -> !value.validated
            else -> value is NetworkState.Idle
        }
    }
}