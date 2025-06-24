@file:Suppress("unused")

package compat.x509

import compat.x509.api.Api
import compat.x509.api.Api29Impl
import compat.x509.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import javax.net.ssl.X509TrustManager

/**
 * @author yuansui
 * @since 2020-04-09
 */
object X509Compat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.Q_29) -> Api29Impl()
        else -> ApiImpl()
    }

    fun getTrustManager(): X509TrustManager {
        return api.getTrustManager()
    }
}