package eth.interceptor

import eth.model.Request

/**
 * 请求预处理拦截器
 */
interface PreInterceptor {

    fun intercept(chain: Chain): Request

    interface Chain {
        fun request(): Request

        fun proceed(request: Request): Request
    }
}