package vector.service

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.util.InjectUtil

abstract class ServiceEx : LifecycleService() {

    companion object {
        const val FOREGROUND_FLAG = "foreground_flag"

        const val DEFAULT_CHANNEL_ID = "00000"
        const val DEFAULT_CHANNEL_NAME = "default_notification_name"
        const val DEFAULT_NOTIFICATION_ID = 100000
    }

    @RequiresApi(Build.VERSION_CODES.P)
    open val buildNotification: ((ForegroundNotificationAttr) -> Unit)? = null

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val isForeground = intent.getBooleanExtra(FOREGROUND_FLAG, false)
            if (isForeground && isSdkAtLeast(SdkInt.P_28)) {
                adaptForeground()
            }

            InjectUtil.bind(this, intent)
            onHandleIntent(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        val isForeground = intent.getBooleanExtra(FOREGROUND_FLAG, false)
        if (isForeground && isSdkAtLeast(SdkInt.P_28)) {
            adaptForeground()
        }

        InjectUtil.bind(this, intent)
        onHandleIntent(intent)
        return super.onBind(intent)
    }

    /**
     * 如果通过bindService启动的服务, 不会走这里
     */
    protected abstract fun onHandleIntent(intent: Intent)

    @RequiresApi(Build.VERSION_CODES.P)
    private fun adaptForeground() {
        val attr = ForegroundNotificationAttr()
        buildNotification?.invoke(attr)

        val channel = NotificationChannelCompat.Builder(
            attr.channelId,
            NotificationManager.IMPORTANCE_LOW
        ).setName(attr.channelName).build()
        val manager = NotificationManagerCompat.from(this)
        manager.createNotificationChannel(channel)

        val n = NotificationCompat.Builder(this, attr.channelId).build()
        startForeground(DEFAULT_NOTIFICATION_ID, n)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    inner class ForegroundNotificationAttr {
        var channelId: String = DEFAULT_CHANNEL_ID
        var channelName: String = DEFAULT_CHANNEL_NAME
        var notificationId: Int = DEFAULT_NOTIFICATION_ID
        var importance: Int = NotificationManager.IMPORTANCE_LOW
    }
}
