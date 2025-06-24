package compat.telephony.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.telephony

/**
 * @author yuansui
 * @since 2023/6/5
 */
@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.O)
internal class Api26Impl : Api by Api23Impl() {

    override fun isEnabled(context: Context): Boolean {
        if (!modifyPhoneStateEnabled(context)) return false
        return context.telephony().isDataEnabled
    }

    override fun enable(context: Context): Boolean {
        if (isAirplaneModeOn(context)) return false
        if (isEnabled(context)) return false
        if (!modifyPhoneStateEnabled(context)) return false
        context.telephony().isDataEnabled = true
        return true
    }

    override fun disable(context: Context): Boolean {
        if (isAirplaneModeOn(context)) return false
        if (!isEnabled(context)) return false
        if (!modifyPhoneStateEnabled(context)) return false
        context.telephony().isDataEnabled = false
        return true
    }
}