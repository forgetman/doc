package tool.trigger.constraints.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.ContextCompat
import logger.L

internal abstract class BroadcastReceiverConstraintTracker<T>(context: Context) : ConstraintTracker<T>(context) {

    companion object {
        private const val LOG_TAG = "BroadcastReceiverConstraintTracker"
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onBroadcastReceive(intent)
        }
    }

    /**
     * Called when the [BroadcastReceiver] is receiving an [Intent] broadcast and should
     * handle the received [Intent].
     *
     * @param intent  The [Intent] being received.
     */
    abstract fun onBroadcastReceive(intent: Intent)

    /**
     * @return The [IntentFilter] associated with this tracker.
     */
    abstract val intentFilter: IntentFilter

    override fun startTracking() {
        L.d(LOG_TAG, "startTracking, ${javaClass.simpleName}: registering receiver")
        ContextCompat.registerReceiver(appContext, broadcastReceiver, intentFilter)
    }

    override fun stopTracking() {
        L.d(LOG_TAG, "stopTracking, ${javaClass.simpleName}: unregistering receiver")
        appContext.unregisterReceiver(broadcastReceiver)
    }
}