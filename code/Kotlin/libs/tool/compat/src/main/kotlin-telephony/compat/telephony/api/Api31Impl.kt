package compat.telephony.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import compat.ext.telephony
import compat.telephony.api.delegate.SimStateDelegate
import compat.telephony.api.delegate.SimStateDelegateImpl
import compat.telephony.def.listener.CallStateListener
import compat.telephony.def.listener.SignalStrengthListener

/**
 * @author yuansui
 * @since 2023/6/5
 */
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
internal class Api31Impl : SimStateDelegate by SimStateDelegateImpl, Api by Api26Impl() {

    private var signalCallback: TelephonyCallback? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }
    private val signalStrengthListeners = mutableListOf<SignalStrengthListener>()

    private var callStateCallback: TelephonyCallback? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }
    private val callStateListeners = mutableListOf<CallStateListener>()


    override fun enable(context: Context): Boolean {
        if (isEnabled(context)) return false
        if (isAirplaneModeOn(context)) return false
        if (!modifyPhoneStateEnabled(context)) return false
        context.telephony().setDataEnabledForReason(TelephonyManager.DATA_ENABLED_REASON_USER, true)
        return true
    }

    override fun disable(context: Context): Boolean {
        if (!isEnabled(context)) return false
        if (isAirplaneModeOn(context)) return false
        if (!modifyPhoneStateEnabled(context)) return false
        context.telephony().setDataEnabledForReason(TelephonyManager.DATA_ENABLED_REASON_USER, false)
        return true
    }

    override fun registerSignalStrengthsListener(context: Context, listener: SignalStrengthListener) {
        if (signalStrengthListeners.contains(listener)) return
        signalStrengthListeners.add(listener)

        if (signalCallback != null) return
        val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {

            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                signalStrengthListeners.forEach { it.onSignalStrengthChanged(signalStrength) }
            }
        }
        context.telephony().registerTelephonyCallback(context.mainExecutor, callback)
        signalCallback = callback
    }

    override fun unregisterSignalStrengthsListener(context: Context, listener: SignalStrengthListener) {
        signalStrengthListeners.remove(listener)
        if (signalStrengthListeners.isEmpty()) {
            signalCallback?.let {
                context.telephony().unregisterTelephonyCallback(it)
                signalCallback = null
            }
        }
    }

    override fun registerCallStateListener(context: Context, listener: CallStateListener) {
        if (callStateListeners.contains(listener)) return
        callStateListeners.add(listener)

        if (callStateCallback != null) return
        val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                callStateListeners.forEach { it.onCallStateChanged(state) }
            }
        }
        context.telephony().registerTelephonyCallback(context.mainExecutor, callback)
        callStateCallback = callback
    }

    override fun unregisterCallStateListener(context: Context, listener: CallStateListener) {
        callStateListeners.remove(listener)
        if (callStateListeners.isEmpty()) {
            callStateCallback?.let {
                context.telephony().unregisterTelephonyCallback(it)
                callStateCallback = null
            }
        }
    }
}