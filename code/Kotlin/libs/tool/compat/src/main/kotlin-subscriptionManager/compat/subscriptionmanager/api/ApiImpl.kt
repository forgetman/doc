package compat.subscriptionmanager.api

import android.content.Context
import compat.subscriptionmanager.SubscriptionManagerCompat

/**
 * FIXME: 低版本api暂时不支持
 */
internal class ApiImpl : Api {
    override fun getDefaultVoiceSubscriptionId(context: Context): Int {
        return SubscriptionManagerCompat.INVALID_SUBSCRIPTION_ID
    }

    override fun getDefaultSmsSubscriptionId(context: Context): Int {
        return SubscriptionManagerCompat.INVALID_SUBSCRIPTION_ID
    }

    override fun getDefaultSubscriptionId(context: Context): Int {
        return SubscriptionManagerCompat.INVALID_SUBSCRIPTION_ID
    }

    override fun getDefaultDataSubscriptionId(context: Context): Int {
        return SubscriptionManagerCompat.INVALID_SUBSCRIPTION_ID
    }
}