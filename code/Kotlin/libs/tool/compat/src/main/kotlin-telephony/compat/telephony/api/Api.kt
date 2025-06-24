package compat.telephony.api

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import compat.ext.telephony
import compat.telephony.def.SimState
import compat.telephony.def.listener.CallStateListener
import compat.telephony.def.listener.SignalStrengthListener
import compat.telephony.def.listener.SimStateListener
import compat.telephony.def.listener.TelephonyStateListener
import sugar.ext.isSystemApplication

internal interface Api {

    /**
     * 开关是否打开
     */
    fun isEnabled(context: Context): Boolean

    /**
     * 打开开关
     */
    fun enable(context: Context): Boolean

    /**
     * 关闭开关
     */
    fun disable(context: Context): Boolean

    fun registerListener(context: Context, listener: TelephonyStateListener)
    fun unregisterListener(context: Context, listener: TelephonyStateListener)

    fun registerSimStateListener(context: Context, listener: SimStateListener)
    fun unregisterSimStateListener(context: Context, listener: SimStateListener)

    fun registerSignalStrengthsListener(context: Context, listener: SignalStrengthListener)
    fun unregisterSignalStrengthsListener(context: Context, listener: SignalStrengthListener)

    fun registerCallStateListener(context: Context, listener: CallStateListener)
    fun unregisterCallStateListener(context: Context, listener: CallStateListener)

    fun getSimState(context: Context): SimState {
        return SimState.acceptSystemState(context.telephony().simState)
    }

    fun getLine1Number(context: Context): String?

    fun getDailyDataUsage(context: Context): Long

    fun isEmergencyNumber(context: Context, number: String): Boolean

    fun getPhoneCount(context: Context): Int
}

internal fun Api.modifyPhoneStateEnabled(context: Context): Boolean {
    if (!context.isSystemApplication(context.packageName)) return false
    val check = context.packageManager.checkPermission(Manifest.permission.MODIFY_PHONE_STATE, context.packageName)
    return check == PackageManager.PERMISSION_GRANTED
}

internal fun Api.isAirplaneModeOn(context: Context): Boolean {
    return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
}

