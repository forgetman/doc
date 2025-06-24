package eth.convertor

import com.google.gson.JsonSyntaxException
import eson.Eson
import eth.ext.paramUpperBound
import eth.model.ErrorDefaultCode
import eth.model.EthException
import eth.model.HttpMethod
import eth.model.Response
import logger.L

/**
 * 解析器
 */
abstract class AbstractConverter : Converter {

    final override fun <T> onResponse(response: Response, eson: Eson): T? {
        val method = response.request.method
        if (method == HttpMethod.DOWNLOAD) return null // 不处理下载的请求类型

        val responseType = response.request.returnType?.paramUpperBound
            ?: return null // 无法获取或没有声明返回类型
        try {
            val convertResult = convert(
                response.code,
                response.body?.content,
                response.request
            ) ?: return null

            if (convertResult.success) {
                val data = convertResult.data ?: throw EthException(
                    ErrorDefaultCode.DATA_NOT_EXIST,
                    "data为null",
                    NullPointerException("data为null")
                )

                val t: Any = when (responseType) {
                    String::class.java -> data
                    Byte::class.java -> data.toByte()
                    Short::class.java -> data.toShort()
                    Char::class.java -> data.toCharArray()
                    Long::class.java -> data.toLong()
                    Float::class.java -> data.toFloat()
                    Double::class.java -> data.toDouble()
                    Boolean::class.java -> when (data) {
                        "0" -> false
                        "1" -> true
                        else -> data.toBooleanStrict()
                    }

                    else -> {
                        eson.fromJson<T>(convertResult.data, responseType)
                            ?: throw EthException(
                                ErrorDefaultCode.PARSE,
                                "Json无法解析",
                                JsonSyntaxException("Json无法解析")
                            )
                    }
                }
                @Suppress("UNCHECKED_CAST")
                return t as T
            } else {
                throw EthException(
                    convertResult.code ?: ErrorDefaultCode.PARSE,
                    convertResult.msg.orEmpty().ifEmpty { "模版解析结果失败, ${response.body?.content}" },
                    convertResult.error
                )
            }
        } catch (e: EthException) {
            // EthException的原样抛出
            throw e
        } catch (t: Throwable) {
            L.e("数据解析失败\n${t.message}")
            throw EthException(ErrorDefaultCode.PARSE, t.message, t)
        }
    }

    abstract fun convert(httpCode: Int, content: String?, request: eth.model.Request): ConvertResult?
}