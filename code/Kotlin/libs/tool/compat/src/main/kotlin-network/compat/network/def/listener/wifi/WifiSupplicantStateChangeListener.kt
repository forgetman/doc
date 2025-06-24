package compat.network.def.listener.wifi

import android.net.wifi.SupplicantState

/**
 * @author yuansui
 * @since 2023/7/17
 */
fun interface WifiSupplicantStateChangeListener {
    fun onSupplicantStateChanged(state: SupplicantState)
}