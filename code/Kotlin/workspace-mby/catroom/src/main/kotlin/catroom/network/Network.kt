package catroom.network

import eth.Eth
import eth.api.impl.OkHttpService
import eth.interceptor.LogInterceptor

object URL {
    const val HOST = "https://api.miaobue.cn"
}

private val NetworkClient: Eth by lazy {
    Eth.builder()
        .baseUrl(URL.HOST)
        .service(OkHttpService.builder().build())
        .addConverter(CommonConverter())
        .addConverter(DownloadConverter())
        .addInterceptor(LogInterceptor())
        .build()
}

internal inline fun <reified T : Any> createApi(): T = NetworkClient.create(T::class)
