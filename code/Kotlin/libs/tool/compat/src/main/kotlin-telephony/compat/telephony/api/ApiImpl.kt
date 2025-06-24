@file:Suppress("DEPRECATION")

package compat.telephony.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.PhoneNumberUtils
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import compat.context.ContextCompat
import compat.ext.checkPermission
import compat.ext.telephony
import compat.network.NetworkCompat
import compat.network.def.NetworkState
import compat.network.def.listener.NetworkListener
import compat.telephony.api.delegate.SimStateDelegate
import compat.telephony.api.delegate.SimStateDelegateImpl
import compat.telephony.def.listener.CallStateListener
import compat.telephony.def.listener.SignalStrengthListener
import compat.telephony.def.listener.SimStateListener
import compat.telephony.def.listener.TelephonyStateListener
import sugar.util.reflectMethod

internal class ApiImpl : SimStateDelegate by SimStateDelegateImpl, Api {

    private var airplaneModeReceiver: BroadcastReceiver? = null
    private var networkListener: NetworkListener? = null

    private val telephonyListeners = mutableListOf<TelephonyStateListener>()

    private var callStateListener: PhoneStateListener? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }
    private val callStateListeners = mutableListOf<CallStateListener>()

    private var signalStrengthListener: PhoneStateListener? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }
    private val signalStrengthListeners = mutableListOf<SignalStrengthListener>()


    override fun isEnabled(context: Context): Boolean {
        if (isAirplaneModeOn(context)) return false
        return checkDataEnabledState(context)
    }

    override fun enable(context: Context): Boolean {
        if (isEnabled(context)) return false
        if (isAirplaneModeOn(context)) return false
        return setDataEnabled(context, true)
    }

    override fun disable(context: Context): Boolean {
        if (!isEnabled(context)) return false
        if (isAirplaneModeOn(context)) return false
        return setDataEnabled(context, false)
    }

    override fun registerListener(context: Context, listener: TelephonyStateListener) {
        if (telephonyListeners.contains(listener)) return
        telephonyListeners.add(listener)
        if (airplaneModeReceiver != null) return

        fun onStateChanged(enabled: Boolean) {
            telephonyListeners.forEach { l ->
                l.onStateChanged(enabled)
            }
        }

        airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (isAirplaneModeOn(context)) {
                    onStateChanged(false)
                }
            }
        }
        ContextCompat.registerReceiver(context, airplaneModeReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        })

        val networkListener = object : NetworkListener {
            override fun onConnectStateChanged(state: NetworkState) {
                if (state is NetworkState.Cellular) {
                    onStateChanged(true)
                } else {
                    onStateChanged(false)
                }
            }
        }
        NetworkCompat.registerListener(context, networkListener)
        this.networkListener = networkListener
    }

    override fun unregisterListener(context: Context, listener: TelephonyStateListener) {
        telephonyListeners.remove(listener)
        if (telephonyListeners.isEmpty()) {
            if (airplaneModeReceiver != null) {
                context.unregisterReceiver(airplaneModeReceiver)
                airplaneModeReceiver = null
            }

            networkListener?.let {
                NetworkCompat.unregisterListener(context, it)
                networkListener = null
            }
        }
    }

    override fun registerSimStateListener(context: Context, listener: SimStateListener) {
        addSimStateListener(context, this, listener)
    }

    override fun unregisterSimStateListener(context: Context, listener: SimStateListener) {
        removeSimStateListener(context, listener)
    }

    override fun registerSignalStrengthsListener(context: Context, listener: SignalStrengthListener) {
        if (signalStrengthListeners.contains(listener)) return
        signalStrengthListeners.add(listener)

        if (signalStrengthListener != null) return
        val phoneStateListener = object : PhoneStateListener() {

            @Deprecated("Deprecated in Java", ReplaceWith("listener.onSignalStrengthsChanged(signalStrength)"))
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                signalStrengthListeners.forEach { listener ->
                    listener.onSignalStrengthChanged(signalStrength)
                }
            }
        }
        context.telephony().listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        signalStrengthListener = phoneStateListener
    }

    override fun unregisterSignalStrengthsListener(context: Context, listener: SignalStrengthListener) {
        signalStrengthListeners.remove(listener)
        if (signalStrengthListeners.isEmpty()) {
            signalStrengthListener?.let {
                context.telephony().listen(it, PhoneStateListener.LISTEN_NONE)
                signalStrengthListener = null
            }
        }
    }

    override fun registerCallStateListener(context: Context, listener: CallStateListener) {
        if (callStateListeners.contains(listener)) return
        callStateListeners.add(listener)

        if (callStateListener != null) return
        val phoneStateListener = object : PhoneStateListener() {

            @Deprecated("Deprecated in Java", ReplaceWith("listener.onSignalStrengthsChanged(signalStrength)"))
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                callStateListeners.forEach { listener ->
                    listener.onCallStateChanged(state)
                }
            }
        }
        context.telephony().listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        callStateListener = phoneStateListener
    }

    override fun unregisterCallStateListener(context: Context, listener: CallStateListener) {
        callStateListeners.remove(listener)
        if (callStateListeners.isEmpty()) {
            callStateListener?.let {
                context.telephony().listen(it, PhoneStateListener.LISTEN_NONE)
                callStateListener = null
            }
        }
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun getLine1Number(context: Context): String? {
        if (!context.checkPermission(Manifest.permission.READ_SMS)) return null
        if (!context.checkPermission(Manifest.permission.READ_PHONE_STATE)) return null
        if (!context.checkPermission(Manifest.permission.READ_PHONE_NUMBERS)) return null
        return context.telephony().line1Number
    }

    override fun getDailyDataUsage(context: Context): Long {
        // 较低的版本没有提供直接的api获取流量, 暂时不处理
        return 0
    }

    override fun isEmergencyNumber(context: Context, number: String): Boolean {
        return PhoneNumberUtils.isEmergencyNumber(number)
    }

    override fun getPhoneCount(context: Context): Int {
        // 低于23的版本没有提供直接的api获取卡槽数量, 暂时不处理
        return 1
    }

    private fun setDataEnabled(context: Context, enabled: Boolean): Boolean {
        return try {
            val t = context.telephony()
            t.reflectMethod("setDataEnabled", Boolean::class.java).apply {
                isAccessible = true
                invoke(t, enabled)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun checkDataEnabledState(context: Context): Boolean {
        try {
            val t = context.telephony()
            t.reflectMethod("getDataEnabled").apply {
                isAccessible = true
                return invoke(t) as Boolean
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}