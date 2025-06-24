package image.glide.http

import com.squareup.okhttp.OkHttpClient
import compat.x509.X509Compat
import javax.net.ssl.SSLContext

/**
 * @author yuansui
 * @since 2018/10/9
 */
object UnsafeOkHttpClient {

    fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustManager = X509Compat.getTrustManager()
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), null)

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslContext.socketFactory, trustManager)
        return builder.build()
    }
}