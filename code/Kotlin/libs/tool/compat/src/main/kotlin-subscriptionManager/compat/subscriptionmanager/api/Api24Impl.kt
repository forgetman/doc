package compat.subscriptionmanager.api

import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
internal class Api24Impl : Api by ApiImpl() {

    override fun getDefaultVoiceSubscriptionId(context: Context): Int {
        return SubscriptionManager.getDefaultVoiceSubscriptionId()
    }

    override fun getDefaultSmsSubscriptionId(context: Context): Int {
        return SubscriptionManager.getDefaultSmsSubscriptionId()
    }

    override fun getDefaultSubscriptionId(context: Context): Int {
        return SubscriptionManager.getDefaultSubscriptionId()
    }

    override fun getDefaultDataSubscriptionId(context: Context): Int {
        return SubscriptionManager.getDefaultDataSubscriptionId()
    }
}