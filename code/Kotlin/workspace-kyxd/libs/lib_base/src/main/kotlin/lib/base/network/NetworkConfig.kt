package lib.base.network

import eth.Eth
import eth.api.impl.OkHttpService
import eth.interceptor.LogInterceptor
import vector.util.PackageUtil
import java.util.concurrent.TimeUnit

inline fun <reified T : Any> createApi(): T = EthInst.create(T::class)

val EthInst: Eth by lazy {
    val baseUrl = PackageUtil.getMetaValue("host").orEmpty()

    Eth.builder()
        .baseUrl(baseUrl)
        .addConverter(CommonConverter())
        .addInterceptor(CommonInterceptor())
        .addInterceptor(SignInterceptor())
        .addInterceptor(LogInterceptor())
        .service(
            OkHttpService.builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .build()
}