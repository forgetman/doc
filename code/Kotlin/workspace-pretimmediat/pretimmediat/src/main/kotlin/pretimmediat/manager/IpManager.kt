package pretimmediat.manager

import android.content.Context
import android.net.ConnectivityManager
import compat.network.NetworkCompat
import logger.L
import sugar.ext.systemService
import vector.singleton.Singleton
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections


/**
 * 管理ip地址的获取
 */
class IpManager private constructor(private val context: Context) {

    companion object : Singleton<Context, IpManager> by Singleton({ context ->
        IpManager(context.applicationContext)
    }) {
        private const val LOG_TAG = "IpManager"
    }

    fun getAddress(): String? {
        return when {
            NetworkCompat.isWifi(context) -> getWiFiIPAddress()
            NetworkCompat.isCellular(context) -> getMobileIPAddress()
            else -> null
        }
    }

    private fun getWiFiIPAddress(): String? {
        val link = context.systemService<ConnectivityManager>()
            .getLinkProperties(NetworkCompat.getActiveNetwork(context)) ?: return null
        L.d(LOG_TAG, "getWiFiIPAddress, link: $link")
        return link.linkAddresses.filter { !it.address.isLoopbackAddress }
            .find {
                it.address is Inet4Address
            }?.address?.hostAddress
    }

    private fun getMobileIPAddress(): String? {
        try {
            for (intf in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (addr in Collections.list(intf.inetAddresses)) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
            return null
        } catch (e: Exception) {
            L.e(LOG_TAG, "getMobileIPAddress", e)
        }
        return null
    }
}