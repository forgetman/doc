package eth.api.impl

import eth.Task
import eth.api.service.HttpService
import eth.model.HttpMethod
import eth.model.Request
import eth.okhttp.interceptor.LogInterceptor
import eth.okhttp.interceptor.RetryInterceptor
import eth.okhttp.task.*
import okhttp.ext.ssl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import sugar.ext.self
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class OkHttpService private constructor(
    private val httpClient: OkHttpClient
) : HttpService {

    companion object {
        private const val TIMEOUT = 30L

        fun builder() = Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)

        fun default() = builder().build()
    }

    override fun newBuilder(): HttpService.Builder {
        return Builder(this)
    }

    override fun sslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager): HttpService {
        return newBuilder().sslSocketFactory(sslSocketFactory, trustManager).build()
    }

    override fun <T> createTask(request: Request): Task<T> {
        return when (request.method) {
            HttpMethod.GET -> GetTask(httpClient, request)
            HttpMethod.POST -> PostTask(httpClient, request)
            HttpMethod.UPLOAD -> UploadTask(httpClient, request)
            HttpMethod.DOWNLOAD -> DownloadTask(httpClient, request)
            HttpMethod.PUT -> PutTask(httpClient, request)
            HttpMethod.DELETE -> DeleteTask(httpClient, request)
        }
    }

    class Builder(service: OkHttpService?) : HttpService.Builder {
        internal constructor() : this(null)

        private var okBuilder: OkHttpClient.Builder = service?.httpClient?.newBuilder()
            ?: OkHttpClient.Builder()
                .addInterceptor(LogInterceptor())
                .addInterceptor(RetryInterceptor())

        private var sslSocketFactory: SSLSocketFactory? = null
        private var trustManager: X509TrustManager? = null


        fun addInterceptor(interceptor: Interceptor) = self {
            okBuilder.addInterceptor(interceptor)
        }

        fun addNetworkInterceptor(interceptor: Interceptor) = self {
            okBuilder.addNetworkInterceptor(interceptor)
        }

        override fun connectTimeout(timeout: Long, unit: TimeUnit) =
            self { okBuilder.connectTimeout(timeout, unit) }

        override fun readTimeout(timeout: Long, unit: TimeUnit) =
            self { okBuilder.readTimeout(timeout, unit) }

        override fun writeTimeout(timeout: Long, unit: TimeUnit) =
            self { okBuilder.writeTimeout(timeout, unit) }

        override fun sslSocketFactory(
            sslSocketFactory: SSLSocketFactory,
            trustManager: X509TrustManager
        ) = self {
            this.sslSocketFactory = sslSocketFactory
            this.trustManager = trustManager
        }

        override fun build(): HttpService {
            okBuilder.ssl(sslSocketFactory, trustManager)
            return OkHttpService(okBuilder.build())
        }
    }
}