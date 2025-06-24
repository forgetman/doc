package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 息屏状态
 */
internal class ScreenOffController(
    tracker: ConstraintTracker<Boolean>
) : ConstraintController<Boolean>(tracker) {

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        return spec.constraints.requiresScreenOff
    }

    override fun isConstrained(value: Boolean) = !value
}