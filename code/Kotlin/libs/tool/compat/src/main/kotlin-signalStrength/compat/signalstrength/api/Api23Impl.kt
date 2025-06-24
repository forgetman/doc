package compat.signalstrength.api

import android.content.Context
import android.os.Build
import android.telephony.SignalStrength
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api by ApiImpl() {

    override fun getSignalLevel(context: Context, signalStrength: SignalStrength): Int {
        return signalStrength.level
    }
}