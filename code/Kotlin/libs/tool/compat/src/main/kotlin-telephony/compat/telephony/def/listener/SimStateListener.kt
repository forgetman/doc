package compat.telephony.def.listener

import compat.telephony.def.SimState

fun interface SimStateListener {
    fun onStateChanged(state: SimState)
}