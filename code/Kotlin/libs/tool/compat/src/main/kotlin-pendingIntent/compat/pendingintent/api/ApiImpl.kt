package compat.pendingintent.api

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * @author yuansui
 * @since 2022/5/25
 */
internal class ApiImpl : Api {

    override fun getBroadcast(
        context: Context,
        requestCode: Int,
        intent: Intent,
        flags: Int
    ): PendingIntent {
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}