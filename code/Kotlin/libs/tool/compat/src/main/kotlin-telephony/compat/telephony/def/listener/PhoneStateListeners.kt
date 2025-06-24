package compat.telephony.def.listener

import android.telephony.SignalStrength

/*******
 * 声明[android.telephony.PhoneStateListener]和[android.telephony.TelephonyCallback]相关的interface
 ******/

/**
 * 信号强度监听器
 */
fun interface SignalStrengthListener {
    fun onSignalStrengthChanged(signalStrength: SignalStrength)
}

/**
 * 电话状态监听器
 */
fun interface CallStateListener {
    /**
     * @param state 以下三种其中之一
     * [android.telephony.TelephonyManager.CALL_STATE_IDLE]]
     * [android.telephony.TelephonyManager.CALL_STATE_RINGING]
     * [android.telephony.TelephonyManager.CALL_STATE_OFFHOOK]
     */
    fun onCallStateChanged(state: Int)
}