package eth.okhttp.interceptor

import eth.def.HeaderKey
import logger.L
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * okHttp的重试拦截器
 */
class RetryInterceptor : Interceptor {

    companion object {
        private const val LOG_TAG = "Network"

        private const val DEFAULT: Int = -1
        private const val DEFAULT_LONG: Long = -1L
    }

    private var count: Int = DEFAULT
    private var delay: Long = DEFAULT_LONG

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        try {
            count = oldRequest.headers[HeaderKey.RETRY_COUNT]?.toInt() ?: DEFAULT
            delay = oldRequest.headers[HeaderKey.RETRY_DELAY]?.toLong() ?: DEFAULT_LONG
        } catch (e: Exception) {
            // do nothing
        }
        if (count == DEFAULT || delay == DEFAULT_LONG) {
            return chain.proceed(oldRequest)
        }

        val newRequest = oldRequest.newBuilder()
            .removeHeader(HeaderKey.RETRY_COUNT)
            .removeHeader(HeaderKey.RETRY_DELAY)
            .build()
        var response: Response? = null
        try {
            // 第一次正式请求
            response = chain.proceed(newRequest)
        } catch (e: Exception) {
            // 防止connection reset异常
            // do nothing
        }

        while (canRetry(response)) {
            try {
                count--
                response?.close()

                Thread.sleep(delay)

                L.groupBy(
                    "重试 -----",
                    "count = $count",
                    "request = $oldRequest"
                ).d(LOG_TAG)

                response = chain.proceed(newRequest)
            } catch (e: Exception) {
                // do nothing
            }
        }

        return response ?: chain.proceed(newRequest)
    }

    private fun canRetry(r: Response?): Boolean {
        return if (r == null) {
            count >= 1
        } else {
            !r.isSuccessful && count >= 1
        }
    }
}
