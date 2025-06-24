package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 电池温度不过高
 */
internal class BatteryTemperatureNotHighController(
    tracker: ConstraintTracker<Boolean>
) : ConstraintController<Boolean>(tracker) {

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        return spec.constraints.requiresTemperatureNotHigh
    }

    override fun isConstrained(value: Boolean) = !value
}