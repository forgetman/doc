package lib.jg

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.jpush.android.api.JPushInterface

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
abstract class BaseJPushReceiver : BroadcastReceiver() {

    final override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras ?: return

        when (intent.action) {
            JPushInterface.ACTION_REGISTRATION_ID -> {
                val regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                onRegistrationId(context, regId)
            }
            JPushInterface.ACTION_MESSAGE_RECEIVED -> {
                val message = bundle.getString(JPushInterface.EXTRA_MESSAGE)
                onMessage(context, message)
            }
            JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
                val msg = bundle.getString(JPushInterface.EXTRA_EXTRA)
//                val notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                onNotification(context, msg)
            }
            JPushInterface.ACTION_NOTIFICATION_OPENED -> {
                val msg = bundle.getString(JPushInterface.EXTRA_EXTRA)
                onOpenNotification(context, msg)
            }
        }
    }

    /**
     * 接收到设备注册id
     *
     * @param id
     */
    open fun onRegistrationId(context: Context, id: String?) {}

    /**
     * 接收到自定义消息
     *
     * @param message
     */
    open fun onMessage(context: Context, message: String?) {}

    /**
     * 接收到通知消息, 会自动使用默认样式弹出到通知栏, 不需要做处理
     *
     * @param message
     */
    open fun onNotification(context: Context, message: String?) {}

    /**
     * 打开通知
     */
    open fun onOpenNotification(context: Context, message: String?) {}
}
