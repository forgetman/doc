package catroom.iot.callback

import androidx.annotation.CallSuper
import com.tencent.iot.explorer.device.java.mqtt.TXMqttRequest
import com.tencent.iot.hub.device.java.core.common.Status
import com.tencent.iot.hub.device.java.core.mqtt.TXMqttActionCallBack
import logger.L

/**
 * @author yuansui
 * @since 2024/7/6
 */
open class MqttActionCallback : TXMqttActionCallBack() {

    companion object {
        private const val LOG_TAG = "MqttActionCallback"
    }

    @CallSuper
    override fun onConnectCompleted(
        status: Status,
        reconnect: Boolean,
        userContext: Any?,
        msg: String,
        cause: Throwable?
    ) {
        var userContextInfo = ""
        if (userContext is TXMqttRequest) {
            userContextInfo = userContext.toString()
        }
        val logInfo = String.format(
            "onConnectCompleted, status[%s], reconnect[%b], userContext[%s], msg[%s]",
            status.name, reconnect, userContextInfo, msg
        )
        L.d(LOG_TAG, "onConnectCompleted, logInfo: $logInfo")
    }

    @CallSuper
    override fun onConnectionLost(cause: Throwable) {
        L.e(LOG_TAG, "onConnectionLost", cause)
    }

    @CallSuper
    override fun onDisconnectCompleted(status: Status, userContext: Any, msg: String, cause: Throwable?) {
        if (cause != null) {
            L.e(LOG_TAG, "onDisconnectCompleted, status: $status, userContext: $userContext, msg: $msg", cause)
        } else {
            L.d(LOG_TAG, "onDisconnectCompleted, status: $status, userContext: $userContext, msg: $msg")
        }
    }
}