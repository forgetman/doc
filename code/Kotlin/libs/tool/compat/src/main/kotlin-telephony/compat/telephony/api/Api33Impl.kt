package compat.telephony.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.RemoteException
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi
import compat.ext.CARRIER_PRIVILEGES
import compat.ext.READ_PRIVILEGED_PHONE_STATE
import compat.ext.checkPermission
import logger.L
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2023/6/5
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class Api33Impl : Api by Api31Impl() {

    @SuppressLint("MissingPermission")
    override fun getLine1Number(context: Context): String? {
        if (!context.checkPermission(Manifest.permission.READ_PHONE_NUMBERS)) return null
        if (!context.checkPermission(String.READ_PRIVILEGED_PHONE_STATE)) return null
        if (!context.checkPermission(String.CARRIER_PRIVILEGES)) return null
        val manager = context.systemService<SubscriptionManager>()
        return try {
            manager.getPhoneNumber(SubscriptionManager.DEFAULT_SUBSCRIPTION_ID)
        } catch (e: RemoteException) {
            L.e(e)
            null
        }
    }
}