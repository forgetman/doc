package catroom.network

import eth.convertor.AbstractConverter
import eth.convertor.ConvertResult
import logger.L
import org.json.JSONObject
import vector.ext.filterNull

private object Tag {
    const val CODE = "code"
    const val ERROR_MSG = "msg"
    const val DATA = "data"
}

private object StatusCode {
    const val OK = "200"
}

class CommonConverter : AbstractConverter() {

    companion object {
        private const val LOG_TAG = "CommonConverter"
    }

    override fun convert(httpCode: Int, content: String?, request: eth.model.Request): ConvertResult {
        L.d(LOG_TAG, content.filterNull())

        val result = ConvertResult()

        if (content.isNullOrEmpty()) {
            result.msg = "服务器错误"
        } else {
            // 外层数据结构
            val obj = JSONObject(content)

            val code = obj.optString(Tag.CODE)
            if (code == StatusCode.OK) {
                result.code = code
                result.success = true
                result.data = obj.optString(Tag.DATA)
            } else {
                result.msg = obj.optString(Tag.ERROR_MSG) ?: "服务器错误"
            }
        }

        return result
    }
}