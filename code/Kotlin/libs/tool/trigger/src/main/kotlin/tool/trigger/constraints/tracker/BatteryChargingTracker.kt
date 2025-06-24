package tool.trigger.constraints.tracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import compat.context.ContextCompat
import logger.L

/**
 * 充电中
 */
internal class BatteryChargingTracker(context: Context) : BroadcastReceiverConstraintTracker<Boolean>(context) {

    companion object {
        private const val LOG_TAG = "BatteryChargingTracker"
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
            return isBatteryChangedIntentCharging(intent)
        }

    override val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            if (Build.VERSION.SDK_INT >= 23) {
                intentFilter.addAction(BatteryManager.ACTION_CHARGING)
                intentFilter.addAction(BatteryManager.ACTION_DISCHARGING)
            } else {
                intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
                intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            return intentFilter
        }

    override fun onBroadcastReceive(intent: Intent) {
        val action = intent.action ?: return
        L.d(LOG_TAG, "onBroadcastReceive, $action")
        when (action) {
            BatteryManager.ACTION_CHARGING -> state = true
            BatteryManager.ACTION_DISCHARGING -> state = false
            Intent.ACTION_POWER_CONNECTED -> state = true
            Intent.ACTION_POWER_DISCONNECTED -> state = false
        }
    }

    private fun isBatteryChangedIntentCharging(intent: Intent): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
        } else {
            intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
        }
    }
}