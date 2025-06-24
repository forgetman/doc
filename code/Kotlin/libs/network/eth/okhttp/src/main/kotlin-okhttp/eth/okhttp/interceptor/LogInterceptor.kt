package eth.okhttp.interceptor

import eth.api.BuildConfig
import logger.L
import logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

internal class LogInterceptor : Interceptor {

    companion object {
        private const val LOG_TAG = "Network"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val startNs = System.nanoTime()
        val response = chain.proceed(request)

        if (BuildConfig.DEBUG) {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            L.groupBy {
                append("Network-Response\n")
                append("request method", request.method)
                append("request url", request.url)
                append(
                    "request charset",
                    request.body?.contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
                )

                appendMap("request headers", response.headers.toMultimap())

                append("response code", response.code)
                append("response millis", tookMs)
            }.d(LOG_TAG)
        }
        return response
    }

    private fun Logger.GroupByBuilder.append(desc: String, content: Any?) {
        if (content != null) {
            append("$desc = $content")
        }
    }

    private fun Logger.GroupByBuilder.appendMap(desc: String, params: Map<String, Any>?) {
        if (params.isNullOrEmpty()) return
        append("\n$desc: ")
        params.forEach {
            append("${it.key} = ${it.value}")
        }
        append("") // FIXME: 不知道为什么要加这个才能换最后一行
    }

}