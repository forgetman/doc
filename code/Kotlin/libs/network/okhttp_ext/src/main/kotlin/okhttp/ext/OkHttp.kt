package okhttp.ext

import compat.x509.X509Compat
import okhttp3.OkHttpClient
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

typealias OkRequestBuilder = okhttp3.Request.Builder
typealias OkRequest = okhttp3.Request
typealias OkResponse = okhttp3.Response

fun OkHttpClient.Builder.ssl(
    sslSocketFactory: SSLSocketFactory?,
    trustManager: X509TrustManager?
): OkHttpClient.Builder {
    if (sslSocketFactory != null && trustManager != null) {
        sslSocketFactory(Tls12SocketFactory(sslSocketFactory), trustManager)
    } else {
        val compatTrustManager = X509Compat.getTrustManager()
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(compatTrustManager), null)
        sslSocketFactory(
            Tls12SocketFactory(sslContext.socketFactory),
            compatTrustManager
        )
    }
    return this
}