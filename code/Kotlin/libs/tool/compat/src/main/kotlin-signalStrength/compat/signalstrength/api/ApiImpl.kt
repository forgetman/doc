@file:Suppress("DEPRECATION")

package compat.signalstrength.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import compat.ext.checkPermission
import compat.ext.telephony

internal class ApiImpl : Api {

    // FIXME: 23以下的没校验过, 暂时先放着, 以后真的用到了再检验
    @SuppressLint("MissingPermission")
    override fun getSignalLevel(context: Context, signalStrength: SignalStrength): Int {
        if (!context.checkPermission(Manifest.permission.READ_PHONE_STATE)) return 0

        return when (context.telephony().networkType) {
            TelephonyManager.NETWORK_TYPE_CDMA -> {
                calculateDbmSignalLength(signalStrength.cdmaDbm)
            }

            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B -> {
                calculateDbmSignalLength(signalStrength.evdoDbm)
            }

            else -> {
                if (signalStrength.isGsm) {
                    calculateGsmSignalLength(signalStrength.gsmSignalStrength)
                } else 0
            }
        }
    }

    private fun calculateGsmSignalLength(gsmSignalStrength: Int): Int {
        // Map the GSM signal strength level to signal length
        return when (gsmSignalStrength) {
            in Int.MIN_VALUE..2 -> 0 // Very weak signal
            in 3..8 -> 1 // Weak signal
            in 9..14 -> 2 // Moderate signal
            in 15..20 -> 3 // Good signal
            else -> 4 // Excellent signal
        }
    }

    private fun calculateDbmSignalLength(dbm: Int): Int {
        // Map the dBm signal strength level to signal length
        return when {
            dbm >= -75 -> 4 // Excellent signal
            dbm >= -85 -> 3 // Good signal
            dbm >= -95 -> 2 // Moderate signal
            dbm >= -105 -> 1 // Weak signal
            else -> 0 // Very weak signal
        }
    }
}