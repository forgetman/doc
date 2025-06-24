package catroom.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.miaomiaobuyi.miaomiaoservice.MainActivity
import logger.L
import vector.util.Launcher

class BootBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val LOG_TAG = "BootBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        L.d(LOG_TAG, "onReceive, action: ${intent.action}")
        Launcher.startActivity(this, MainActivity::class)
    }
}
