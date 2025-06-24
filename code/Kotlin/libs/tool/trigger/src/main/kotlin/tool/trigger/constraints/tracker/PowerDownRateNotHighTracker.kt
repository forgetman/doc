package tool.trigger.constraints.tracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import compat.context.ContextCompat
import logger.L
import sugar.ext.systemService
import java.util.concurrent.TimeUnit

/**
 * 灭屏掉电率
 */
internal class PowerDownRateNotHighTracker(context: Context) : BroadcastReceiverConstraintTracker<Boolean>(context) {

    companion object {
        private const val LOG_TAG = "PowerDownRateNotHighTracker"
        private const val TIME_INTERVAL = 10L
    }

    private var currLevel: Int = -1
    private var startTime: Long = -1
    private var screenOff: Boolean = false

    override val initialState: Boolean
        get() {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = ContextCompat.registerReceiver(appContext, null, intentFilter)
            if (intent == null) {
                L.d(LOG_TAG, "getInitialState - null intent received")
                return true
            }

            val pm = appContext.systemService<PowerManager>()
            screenOff = !pm.isInteractive
            if (screenOff) {
                startTime = System.currentTimeMillis()
            }

            return isPowerOffRateNotHigh(intent)
        }

    override val intentFilter: IntentFilter
        get() = IntentFilter(Intent.ACTION_BATTERY_CHANGED).apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

    override fun onBroadcastReceive(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                screenOff = false
                startTime = -1
            }

            Intent.ACTION_SCREEN_OFF -> {
                screenOff = true
                startTime = System.currentTimeMillis()
            }

            Intent.ACTION_BATTERY_CHANGED -> {
                state = isPowerOffRateNotHigh(intent)
            }
        }
    }

    private fun isPowerOffRateNotHigh(intent: Intent): Boolean {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val actualLevel = if (scale == 100) level else (level / (scale * 100f)).toInt()
        L.d(LOG_TAG, "isPowerOffRateNotHigh, actualLevel = $actualLevel")

        if (!screenOff) {
            // 亮屏不统计, 只更新
            currLevel = actualLevel
            return true
        }

        if (currLevel == -1) {
            // 初始化
            currLevel = actualLevel
            return true
        } else {
            /**
             * 通过[TIME_INTERVAL]分钟的掉率来观察高低
             */
            val now = System.currentTimeMillis()
            val timeInterval = now - startTime
            if (timeInterval < TimeUnit.MINUTES.toMinutes(TIME_INTERVAL)) {
                return true
            }
            startTime = now

            val diffLevel = currLevel - actualLevel
            currLevel = actualLevel
            return diffLevel <= 0
        }
    }
}