package tool.trigger.constraints

import logger.L
import tool.trigger.TriggerSpec
import tool.trigger.constraints.controller.BatteryChargingController
import tool.trigger.constraints.controller.BatteryTemperatureNotHighController
import tool.trigger.constraints.controller.ConstraintController
import tool.trigger.constraints.controller.NetworkConnectedController
import tool.trigger.constraints.controller.PowerDownRateNotHighController
import tool.trigger.constraints.controller.ScreenOffController
import tool.trigger.constraints.controller.ScreenOnController
import tool.trigger.constraints.tracker.Trackers

internal interface TriggerConstraintsTracker {
    fun replace(specs: Iterable<TriggerSpec>)
    fun reset()
}

internal interface TriggerConstraintsCallback {
    fun onAllConstraintsMet(specs: List<TriggerSpec>)
    fun onAllConstraintsNotMet(specs: List<TriggerSpec>)
}

internal class TriggerConstraintsTrackerImpl internal constructor(
    private val callback: TriggerConstraintsCallback?,
    private val constraintControllers: Array<ConstraintController<*>>,
) : TriggerConstraintsTracker, ConstraintController.OnConstraintUpdatedCallback {

    companion object {
        private const val LOG_TAG = "TriggerConstraintsTracker"
    }

    private val lock: Any = Any()

    constructor(trackers: Trackers, callback: TriggerConstraintsCallback?) : this(
        callback,
        buildList {
            trackers.networkStateTracker?.let { add(NetworkConnectedController(it)) }
            trackers.batteryChargingTracker?.let { add(BatteryChargingController(it)) }
            trackers.batteryTemperatureNotHighTracker?.let { add(BatteryTemperatureNotHighController(it)) }
            trackers.screenOnTracker?.let { add(ScreenOnController(it)) }
            trackers.screenOffTracker?.let { add(ScreenOffController(it)) }
            trackers.powerDownRateNotHighTracker?.let { add(PowerDownRateNotHighController(it)) }
        }.toTypedArray()
    )

    override fun replace(specs: Iterable<TriggerSpec>) {
        synchronized(lock) {
            for (controller in constraintControllers) {
                controller.callback = null
            }
            for (controller in constraintControllers) {
                controller.replace(specs)
            }
            for (controller in constraintControllers) {
                controller.callback = this
            }
        }
    }

    override fun reset() {
        synchronized(lock) {
            for (controller in constraintControllers) {
                controller.reset()
            }
        }
    }

    private fun areAllConstraintsMet(spec: TriggerSpec): Boolean {
        synchronized(lock) {
            val controller = constraintControllers.firstOrNull {
                it.isSpecConstrained(spec)
            }
            if (controller != null) {
                L.d(LOG_TAG, "areAllConstraintsMet, $spec constrained by ${controller.javaClass.simpleName}")
            }
            return controller == null
        }
    }

    override fun onConstraintMet(specs: List<TriggerSpec>) {
        synchronized(lock) {
            val unconstrainedSpecs = specs.filter { areAllConstraintsMet(it) }
            unconstrainedSpecs.forEach {
                L.d(LOG_TAG, "onConstraintMet, Constraints met for $it")
            }
            callback?.onAllConstraintsMet(unconstrainedSpecs)
        }
    }

    override fun onConstraintNotMet(specs: List<TriggerSpec>) {
        synchronized(lock) { callback?.onAllConstraintsNotMet(specs) }
    }

}