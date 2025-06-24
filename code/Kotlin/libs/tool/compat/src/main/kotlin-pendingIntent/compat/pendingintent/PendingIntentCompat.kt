package compat.pendingintent

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import compat.pendingintent.api.Api
import compat.pendingintent.api.Api23Impl
import compat.pendingintent.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2022/5/25
 */
object PendingIntentCompat {

    private val opt: Api = when {
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun getBroadcast(
        context: Context,
        requestCode: Int,
        intent: Intent,
        flags: Int
    ): PendingIntent {
        return opt.getBroadcast(context, requestCode, intent, flags)
    }

}