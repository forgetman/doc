package tool.trigger.constraints.tracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import compat.context.ContextCompat
import logger.L

/**
 * 温度不超出40
 */
internal class BatteryTemperatureNotHighTracker(context: Context) :
    BroadcastReceiverConstraintTracker<Boolean>(context) {

    companion object {
        private const val LOG_TAG = "BatteryTemperatureNotHighTracker"

        private const val HIGH_TEMPERATURE = 40
    }

    override val initialState: Boolean
        get() {
            /**
             * [BatteryManager.ACTION_CHARGING] and [BatteryManager.ACTION_DISCHARGING] are not sticky broadcasts, so
             * we use [Intent.ACTION_BATTERY_CHANGED] on all APIs to get the initial state.
             */
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = ContextCompat.registerReceiver(appContext, null, intentFilter)
            if (intent == null) {
                L.d(LOG_TAG, "getInitialState - null intent received")
                return false
            }
            return isTemperatureHigh(intent)
        }

    override val intentFilter: IntentFilter
        get() = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    override fun onBroadcastReceive(intent: Intent) {
        state = isTemperatureHigh(intent)
    }

    private fun isTemperatureHigh(intent: Intent): Boolean {
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        L.d(LOG_TAG, "isTemperatureHigh, temperature = $temperature")
        return temperature.div(10) < HIGH_TEMPERATURE // 温度没小数, 36.1会返回361
    }
}