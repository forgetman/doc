package tool.trigger.constraints.tracker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import sugar.ext.systemService

/**
 * 亮屏
 */
internal class ScreenOnTracker(context: Context) :
    BroadcastReceiverConstraintTracker<Boolean>(context) {

    override val initialState: Boolean
        get() {
            val pm = appContext.systemService<PowerManager>()
            return pm.isInteractive
        }

    override val intentFilter: IntentFilter
        get() = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }

    override fun onBroadcastReceive(intent: Intent) {
        state = intent.action == Intent.ACTION_SCREEN_ON
    }
}