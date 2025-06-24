package compat.telephony.api

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.telephony

/**
 * @author yuansui
 * @since 2023/6/5
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class Api29Impl : Api by Api26Impl() {

    override fun isEmergencyNumber(context: Context, number: String): Boolean {
        return context.telephony().isEmergencyNumber(number)
    }
}