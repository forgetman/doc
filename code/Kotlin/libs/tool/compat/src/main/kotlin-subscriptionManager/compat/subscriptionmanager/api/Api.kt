package compat.subscriptionmanager.api

import android.content.Context

internal interface Api {
    fun getDefaultVoiceSubscriptionId(context: Context): Int
    fun getDefaultSmsSubscriptionId(context: Context): Int
    fun getDefaultSubscriptionId(context: Context): Int
    fun getDefaultDataSubscriptionId(context: Context): Int
}