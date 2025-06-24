package eth.interceptor

import eth.api.BuildConfig
import eth.model.Request
import logger.L
import logger.Logger

/**
 * 打印log的拦截器
 *
 * @author : GuoXuan
 * @since : 2019/7/12
 */
class LogInterceptor : PreInterceptor {

    companion object {
        private const val LOG_TAG = "Network"
    }

    override fun intercept(chain: PreInterceptor.Chain): Request {
        val request = chain.request()
        if (BuildConfig.DEBUG) {
            L.groupBy {
                append("method", request.method.name)
                append("url", request.url)
                append("contentType", request.contentType.desc)
                append("charset", request.charset)
                append("returnType", request.returnType)
                appendMap("headers", request.headers)
                appendMap("params", request.params)
                appendMap("custom params", request.customizeParams)
                appendLn("body", request.body)
            }.d(LOG_TAG)
        }
        return chain.proceed(request)
    }

    private fun Logger.GroupByBuilder.append(desc: String, content: Any?) {
        if (content != null) {
            append("$desc = $content")
        }
    }

    private fun Logger.GroupByBuilder.appendLn(desc: String, content: Any?) {
        if (content != null) {
            append("\n$desc: ")
            append(content.toString())
        }
    }

    private fun Logger.GroupByBuilder.appendMap(desc: String, params: Map<String, Any>?) {
        if (params.isNullOrEmpty()) return
        append("\n$desc: ")
        params.forEach {
            append("${it.key} = ${it.value}")
        }
    }
}