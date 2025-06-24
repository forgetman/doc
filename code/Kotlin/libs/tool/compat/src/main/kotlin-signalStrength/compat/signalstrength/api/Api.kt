package compat.signalstrength.api

import android.content.Context
import android.telephony.SignalStrength

/**
 * 信号强度相关API
 */
interface Api {
    /**
     * 获取信号强度
     */
    fun getSignalLevel(context: Context, signalStrength: SignalStrength): Int
}