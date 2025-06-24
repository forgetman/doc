package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 充电状态
 */
internal class BatteryChargingController(
    tracker: ConstraintTracker<Boolean>
) : ConstraintController<Boolean>(tracker) {

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        return spec.constraints.requiresCharging
    }

    override fun isConstrained(value: Boolean) = !value
}