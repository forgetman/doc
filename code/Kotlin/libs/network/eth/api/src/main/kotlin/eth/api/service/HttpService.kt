package eth.api.service

import eth.Task
import eth.model.Request
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HttpService {
    fun <T> createTask(request: Request): Task<T>

    fun newBuilder(): Builder

    interface Builder {
        fun connectTimeout(timeout: Long, unit: TimeUnit): Builder

        fun readTimeout(timeout: Long, unit: TimeUnit): Builder

        fun writeTimeout(timeout: Long, unit: TimeUnit): Builder

        fun sslSocketFactory(
            sslSocketFactory: SSLSocketFactory,
            trustManager: X509TrustManager
        ): Builder

        fun build(): HttpService
    }

    fun sslSocketFactory(
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager
    ): HttpService
}