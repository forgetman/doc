package tool.trigger.constraints.tracker

import compat.network.def.NetworkState


/**
 * @author yuansui
 * @since 2023/4/17
 */
internal class Trackers(
    val networkStateTracker: ConstraintTracker<NetworkState>?,
    val batteryChargingTracker: ConstraintTracker<Boolean>?,
    val batteryTemperatureNotHighTracker: ConstraintTracker<Boolean>?,
    val screenOnTracker: ConstraintTracker<Boolean>?,
    val screenOffTracker: ConstraintTracker<Boolean>?,
    val powerDownRateNotHighTracker: ConstraintTracker<Boolean>?,
)