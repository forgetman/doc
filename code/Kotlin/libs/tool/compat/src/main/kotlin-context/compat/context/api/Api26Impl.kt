package compat.context.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import compat.context.def.ReceiverFlags

/**
 * @author yuansui
 * @since 2023/3/21
 */
@RequiresApi(Build.VERSION_CODES.O)
internal class Api26Impl : Api by ApiImpl() {

    override fun registerReceiver(
        context: Context,
        receiver: BroadcastReceiver?,
        filter: IntentFilter,
        flags: ReceiverFlags
    ): Intent? {
        return context.registerReceiver(
            receiver,
            filter,
            when (flags) {
                ReceiverFlags.RECEIVER_VISIBLE_TO_INSTANT_APPS -> Context.RECEIVER_VISIBLE_TO_INSTANT_APPS
                else -> 0x4 // 使用[Context.RECEIVER_NOT_EXPORTED]的值, 默认不导出
            }
        )
    }
}