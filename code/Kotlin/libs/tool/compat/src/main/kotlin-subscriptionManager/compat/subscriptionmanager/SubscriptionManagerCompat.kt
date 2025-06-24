package compat.subscriptionmanager

import android.content.Context
import compat.subscriptionmanager.api.Api
import compat.subscriptionmanager.api.Api24Impl
import compat.subscriptionmanager.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

object SubscriptionManagerCompat {

    const val INVALID_SUBSCRIPTION_ID = -1

    @Suppress("MemberVisibilityCanBePrivate")
    const val DEFAULT_SUBSCRIPTION_ID: Int = Int.MAX_VALUE
    const val MAX_SUBSCRIPTION_ID_VALUE = DEFAULT_SUBSCRIPTION_ID - 1;


    private val api: Api = when {
        isSdkAtLeast(SdkInt.N_24) -> Api24Impl()
        else -> ApiImpl()
    }

    fun getDefaultDataSubscriptionId(context: Context): Int = api.getDefaultDataSubscriptionId(context)
}