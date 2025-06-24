package compat.telephony.def.listener

fun interface TelephonyStateListener {
    fun onStateChanged(enabled: Boolean)
}