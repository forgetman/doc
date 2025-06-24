package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 掉电率
 */
internal class PowerDownRateNotHighController(
    tracker: ConstraintTracker<Boolean>
) : ConstraintController<Boolean>(tracker) {

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        return spec.constraints.requiresPowerDownRateNotHigh
    }

    override fun isConstrained(value: Boolean) = !value
}