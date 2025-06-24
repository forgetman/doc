package compat.context.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.def.ReceiverFlags

/**
 * @author yuansui
 * @since 2023/3/21
 */
internal interface Api {

    fun registerReceiver(
        context: Context,
        receiver: BroadcastReceiver?,
        filter: IntentFilter,
        flags: ReceiverFlags
    ): Intent?
}