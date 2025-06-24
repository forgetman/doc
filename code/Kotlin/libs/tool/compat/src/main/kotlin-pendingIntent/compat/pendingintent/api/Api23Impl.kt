package compat.pendingintent.api

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author yuansui
 * @since 2022/5/25
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class Api23Impl : Api {

    override fun getBroadcast(
        context: Context,
        requestCode: Int,
        intent: Intent,
        flags: Int
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }
}