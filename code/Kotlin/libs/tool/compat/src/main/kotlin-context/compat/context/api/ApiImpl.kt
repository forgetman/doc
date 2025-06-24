package compat.context.api

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.def.ReceiverFlags

internal class ApiImpl : Api {

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun registerReceiver(
        context: Context,
        receiver: BroadcastReceiver?,
        filter: IntentFilter,
        flags: ReceiverFlags
    ): Intent? {
        return context.registerReceiver(receiver, filter)
    }
}