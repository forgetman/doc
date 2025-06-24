package lib.base.network

import eth.convertor.AbstractConverter
import eth.convertor.ConvertResult
import eth.model.Request
import logger.L
import org.json.JSONObject

/**
 * @author yuansui
 * @since 2019-07-11
 */
class CommonConverter : AbstractConverter() {

    private object Tag {
        const val CODE = "error_code"
        const val MSG = "msg"
        const val DATA = "data"
    }

    private object ErrorCode {
        const val OK = "0"
    }

    override fun convert(httpCode: Int, content: String?, request: Request): ConvertResult {
        val result = ConvertResult()

        if (content.isNullOrEmpty()) {
            result.msg = "服务器错误"
            return result
        }

        L.d(content)

        val obj = JSONObject(content)
        val serviceCode = obj.optString(Tag.CODE)
        if (ErrorCode.OK == serviceCode) {
            result.success = true
            result.code = serviceCode
            result.data = obj.optString(Tag.DATA)
        } else {
            result.msg = obj.optString(Tag.MSG)
        }

        return result
    }
}