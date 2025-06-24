package reader.network

import eth.convertor.AbstractConverter
import eth.convertor.ConvertResult
import logger.L
import org.json.JSONObject
import vector.ext.filterNull

private object Tag {
    const val CODE = "status"
    const val INFO = "info"
    const val DATA = "data"
}

private object StatusCode {
    const val OK = "success"
}

class CommonConverter : AbstractConverter() {

    override fun convert(httpCode: Int, content: String?, request: eth.model.Request): ConvertResult {
        L.d(content.filterNull())

        val result = ConvertResult()

        if (content.isNullOrEmpty()) {
            result.msg = "服务器错误"
        } else {
            // 外层数据结构
            val obj = JSONObject(content)

            val info = obj.optString(Tag.INFO)
            if (StatusCode.OK == info) {
                result.code = obj.optString(Tag.CODE)
                result.success = true
                result.data = obj.optString(Tag.DATA)
            } else {
                result.msg = "服务器错误"
            }
        }

        return result
    }
}