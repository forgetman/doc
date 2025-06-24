package test.network

import eth.Eth
import eth.annotation.Charset
import eth.annotation.ContentType
import eth.annotation.method.Download
import eth.annotation.method.Get
import eth.annotation.method.Post
import eth.annotation.param.Body
import eth.annotation.param.Url
import eth.api.impl.OkHttpService
import eth.convertor.DownloadResult
import eth.interceptor.LogInterceptor
import eth.model.CharsetValue
import eth.model.ContentTypeValue
import kotlinx.coroutines.flow.Flow

inline fun <reified T : Any> createApi(): T = EthInst.create(T::class)

val EthInst: Eth by lazy {
    Eth.builder()
        .baseUrl("http://www.baidu.com/")
        .service(OkHttpService.builder().build())
        .addConverter(CommonConverter())
        .addConverter(DownloadConverter())
        .addInterceptor(LogInterceptor())
        .build()
}

interface Api {

    @Get
    fun test2(): Flow<String>

    @Download
    fun downloadApk(@Url url: String?): Flow<DownloadResult>

    @Post
    @ContentType(ContentTypeValue.JSON)
    @Charset(CharsetValue.UTF8)
    fun toJson(@Body list: List<String>): Flow<Int>

    @Get
    fun test3(): Flow<String>
}