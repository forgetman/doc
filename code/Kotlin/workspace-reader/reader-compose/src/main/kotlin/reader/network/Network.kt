package reader.network

import eth.Eth
import eth.api.impl.OkHttpService
import eth.interceptor.LogInterceptor

internal const val RETRY_COUNT = 3
internal const val RETRY_DELAY = 300L

object URL {
    const val HOST = "https://kanapi.jiaston.com/book/"
    const val HOST_SEARCH = "https://sou.jiaston.com/"
    const val HOST_TOP = "https://shuapi.jiaston.com/"
    const val HOST_CATEGORY = "https://appbdsc.cdn.bcebos.com/Categories/"
    const val HOST_INFO = "https://shuapi.jiaston.com/"
}

internal inline fun <reified T : Any> createApi(): T = EthInst.create(T::class)

internal val EthInst: Eth by lazy {
    Eth.builder().baseUrl(URL.HOST)
        .service(OkHttpService.builder().build())
        .addConverter(CommonConverter())
        .addInterceptor(LogInterceptor())
        .build()
}