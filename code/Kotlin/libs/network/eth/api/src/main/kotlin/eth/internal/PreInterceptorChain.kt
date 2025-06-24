package eth.internal

import eth.interceptor.PreInterceptor
import eth.model.Request

/**
 * 内置预拦截器的遍历器
 */
internal class PreInterceptorChain constructor(
    private val interceptors: List<PreInterceptor>,
    private val index: Int,
    private val request: Request
) : PreInterceptor.Chain {

    override fun request(): Request {
        return request
    }

    override fun proceed(request: Request): Request {
        val nextChain = PreInterceptorChain(interceptors, index + 1, request)
        return if (index < interceptors.size && index >= 0) {
            val interceptor = interceptors[index]
            interceptor.intercept(nextChain)
        } else {
            request
        }
    }
}