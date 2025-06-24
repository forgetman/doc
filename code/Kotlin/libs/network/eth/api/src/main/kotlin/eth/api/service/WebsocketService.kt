package eth.api.service

import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author yuansui
 * @since 2023/4/6
 */
interface WebsocketService {
    fun newBuilder(): Builder

    interface Builder {
        fun connectTimeout(timeout: Long, unit: TimeUnit): Builder

        fun readTimeout(timeout: Long, unit: TimeUnit): Builder

        fun writeTimeout(timeout: Long, unit: TimeUnit): Builder

        fun sslSocketFactory(
            sslSocketFactory: SSLSocketFactory,
            trustManager: X509TrustManager
        ): HttpService.Builder

        fun build(): WebsocketService
    }

    fun connect()
}