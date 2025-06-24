@file:Suppress("DEPRECATION")

package compat.telephony.api

import android.Manifest
import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.RemoteException
import androidx.annotation.RequiresApi
import compat.ext.READ_PRIVILEGED_PHONE_STATE
import compat.ext.checkPermission
import compat.ext.networkStats
import compat.ext.telephony
import logger.L
import java.util.Calendar

/**
 * @author yuansui
 * @since 2023/6/5
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api by ApiImpl() {

    companion object {
        private const val LOG_TAG = "telephony_Api23Impl"
    }

    override fun getDailyDataUsage(context: Context): Long {
        if (!context.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) return 0
        if (!context.checkPermission(Manifest.permission.PACKAGE_USAGE_STATS)) return 0

        var totalUsage = 0L
        try {
            val networkStats = context.networkStats().querySummary(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(context),
                getStartOfDayInMillis(),
                System.currentTimeMillis()
            )
            while (networkStats.hasNextBucket()) {
                val bucket = NetworkStats.Bucket()
                networkStats.getNextBucket(bucket)
                totalUsage += bucket.rxBytes + bucket.txBytes
            }
            networkStats.close()
        } catch (e: RemoteException) {
            L.e(LOG_TAG, "getDailyDataUsage", e)
        }

        return totalUsage
    }

    override fun getPhoneCount(context: Context): Int {
        return context.telephony().phoneCount
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSubscriberId(context: Context): String? {
        if (!context.checkPermission(String.READ_PRIVILEGED_PHONE_STATE)) return null
        return context.telephony().subscriberId
    }

    private fun getStartOfDayInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}