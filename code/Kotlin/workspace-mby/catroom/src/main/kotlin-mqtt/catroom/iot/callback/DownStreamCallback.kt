package catroom.iot.callback

import androidx.annotation.CallSuper
import com.tencent.iot.explorer.device.java.data_template.TXDataTemplateDownStreamCallBack
import org.json.JSONObject

/**
 * @author yuansui
 * @since 2024/7/6
 */
open class DownStreamCallback : TXDataTemplateDownStreamCallBack() {

    companion object {
        private const val LOG_TAG = "DownStreamCallback"
    }

    override fun onReplyCallBack(msg: String) {
        //可根据自己需求进行处理属性上报以及事件的回复，根据需求填写
    }

    override fun onGetStatusReplyCallBack(data: JSONObject) {
        //可根据自己需求进行处理状态和控制信息的获取结果
    }

    @CallSuper
    override fun onControlCallBack(msg: JSONObject): JSONObject {
        return buildResponseJson()
    }

    @CallSuper
    override fun onActionCallBack(actionId: String, params: JSONObject): JSONObject {
        return buildResponseJson()
    }

    override fun onUnbindDeviceCallBack(msg: String) {
        // do nothing
    }

    override fun onBindDeviceCallBack(msg: String) {
        // do nothing
    }

    private fun buildResponseJson(): JSONObject {
        val result = JSONObject()
        result.put("code", 0)
        result.put("status", "ok")
        return result
    }
}