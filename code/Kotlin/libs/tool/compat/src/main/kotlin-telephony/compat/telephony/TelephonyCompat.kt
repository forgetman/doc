package compat.telephony

import android.content.Context
import compat.telephony.api.Api
import compat.telephony.api.Api23Impl
import compat.telephony.api.Api26Impl
import compat.telephony.api.Api29Impl
import compat.telephony.api.Api30Impl
import compat.telephony.api.Api31Impl
import compat.telephony.api.Api33Impl
import compat.telephony.api.ApiImpl
import compat.telephony.def.SimState
import compat.telephony.def.listener.CallStateListener
import compat.telephony.def.listener.SignalStrengthListener
import compat.telephony.def.listener.SimStateListener
import compat.telephony.def.listener.TelephonyStateListener
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2023/6/5
 */
object TelephonyCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        isSdkAtLeast(SdkInt.S_31) -> Api31Impl()
        isSdkAtLeast(SdkInt.R_30) -> Api30Impl()
        isSdkAtLeast(SdkInt.Q_29) -> Api29Impl()
        isSdkAtLeast(SdkInt.O_26) -> Api26Impl()
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun isEnabled(context: Context): Boolean = api.isEnabled(context)

    fun enable(context: Context): Boolean = api.enable(context)

    fun disable(context: Context): Boolean = api.disable(context)

    fun registerListener(context: Context, listener: TelephonyStateListener) = api.registerListener(context, listener)

    fun unregisterListener(context: Context, listener: TelephonyStateListener) =
        api.unregisterListener(context, listener)

    fun getSimState(context: Context): SimState = api.getSimState(context)

    fun registerSimStateListener(context: Context, listener: SimStateListener) =
        api.registerSimStateListener(context, listener)

    fun unregisterSimStateListener(context: Context, listener: SimStateListener) =
        api.unregisterSimStateListener(context, listener)

    fun registerSignalStrengthsListener(context: Context, listener: SignalStrengthListener) =
        api.registerSignalStrengthsListener(context, listener)

    fun unregisterSignalStrengthsListener(context: Context, listener: SignalStrengthListener) =
        api.unregisterSignalStrengthsListener(context, listener)

    fun registerCallStateListener(context: Context, listener: CallStateListener) =
        api.registerCallStateListener(context, listener)

    fun unregisterCallStateListener(context: Context, listener: CallStateListener) =
        api.unregisterCallStateListener(context, listener)

    /**
     * 获取sim卡号码
     */
    fun getLine1Number(context: Context): String? = api.getLine1Number(context)

    /**
     * 获取每日数据使用量
     * @return 如果没有数据使用量，则返回0，否则返回使用量（字节）
     */
    fun getDailyDataUsage(context: Context): Long = api.getDailyDataUsage(context)

    /**
     * 是否紧急号码
     */
    fun isEmergencyNumber(context: Context, number: String): Boolean = api.isEmergencyNumber(context, number)

    /**
     * Returns the number of logical modems currently configured to be activated.
     *
     * Returns 0 if none of voice, sms, data is not supported
     * Returns 1 for Single standby mode (Single SIM functionality).
     * Returns 2 for Dual standby mode (Dual SIM functionality).
     * Returns 3 for Tri standby mode (Tri SIM functionality).
     */
    fun getPhoneCount(context: Context): Int = api.getPhoneCount(context)
}