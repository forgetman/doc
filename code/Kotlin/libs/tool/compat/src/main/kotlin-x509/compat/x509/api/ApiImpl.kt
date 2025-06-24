package compat.x509.api

import android.annotation.SuppressLint
import java.security.cert.CertificateException
import javax.net.ssl.X509TrustManager

/**
 * @author yuansui
 * @since 2020-04-09
 */
class ApiImpl : Api {

    @SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
    override fun getTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        }
    }
}