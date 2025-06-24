package pretimmediat.network

import eth.convertor.AbstractConverter
import eth.convertor.ConvertResult
import logger.L
import org.json.JSONObject
import vector.ext.filterNull

private object Tag {
    const val CODE = "passiveExampleBackGayBedclothes"
    const val ERROR_MSG = "socialBellyHelpfulPauseCertainCurrency"
    const val DATA = "freeTrafficEgyptianLeague"
}

object StatusCode {
    const val OK = "1000"
    const val ERROR_TOKEN = "-1001" // 需要登录或令牌失效
    const val ERROR = "-1000" // 失败
}

class CommonConverter : AbstractConverter() {

    companion object {
        private const val LOG_TAG = "CommonConverter"
    }

    override fun convert(
        httpCode: Int,
        content: String?,
        request: eth.model.Request
    ): ConvertResult {
        L.d(LOG_TAG, content.filterNull())

        val result = ConvertResult()

        if (content.isNullOrEmpty()) {
            result.msg = "服务器错误"
        } else {
            // 外层数据结构
            val obj = JSONObject(content)

            val code = obj.optString(Tag.CODE)
            result.code = code
            if (code == StatusCode.OK) {
                result.success = true
                result.data = obj.optString(Tag.DATA)
            } else {
                result.msg = obj.optString(Tag.ERROR_MSG) ?: "服务器错误"
            }
        }

        return result
    }
}