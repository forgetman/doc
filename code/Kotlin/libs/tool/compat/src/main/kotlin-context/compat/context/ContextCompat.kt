package compat.context

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.api.Api
import compat.context.api.Api26Impl
import compat.context.api.Api33Impl
import compat.context.api.ApiImpl
import compat.context.def.ReceiverFlags
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2023/3/21
 */
object ContextCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.T_33) -> Api33Impl()
        isSdkAtLeast(SdkInt.O_26) -> Api26Impl()
        else -> ApiImpl()
    }

    fun registerReceiver(
        context: Context,
        receiver: BroadcastReceiver?,
        filter: IntentFilter,
        flags: ReceiverFlags = ReceiverFlags.RECEIVER_NOT_EXPORTED
    ): Intent? {
        return api.registerReceiver(context, receiver, filter, flags)
    }
}