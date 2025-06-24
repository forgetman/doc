package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * 亮屏状态
 */
internal class ScreenOnController(
    tracker: ConstraintTracker<Boolean>
) : ConstraintController<Boolean>(tracker) {

    override fun hasConstraint(spec: TriggerSpec): Boolean {
        return spec.constraints.requiresScreenOn
    }

    override fun isConstrained(value: Boolean) = !value
}