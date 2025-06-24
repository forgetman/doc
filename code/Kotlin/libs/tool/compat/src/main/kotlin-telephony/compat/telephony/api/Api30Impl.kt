package compat.telephony.api

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.telephony

@RequiresApi(Build.VERSION_CODES.R)
class Api30Impl : Api by Api29Impl() {

    override fun getPhoneCount(context: Context): Int {
        return context.telephony().activeModemCount
    }
}