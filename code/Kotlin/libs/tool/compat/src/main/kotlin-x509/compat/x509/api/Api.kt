package compat.x509.api

import javax.net.ssl.X509TrustManager

/**
 * @author yuansui
 * @since 2020-04-09
 */
internal interface Api {
    fun getTrustManager(): X509TrustManager
}