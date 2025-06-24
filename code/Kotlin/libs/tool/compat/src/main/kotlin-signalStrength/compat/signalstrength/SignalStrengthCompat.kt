package compat.signalstrength

import android.content.Context
import android.telephony.SignalStrength
import compat.signalstrength.api.Api
import compat.signalstrength.api.Api23Impl
import compat.signalstrength.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2024/6/16
 */
object SignalStrengthCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun getSignalLevel(context: Context, signalStrength: SignalStrength): Int =
        api.getSignalLevel(context, signalStrength)
}