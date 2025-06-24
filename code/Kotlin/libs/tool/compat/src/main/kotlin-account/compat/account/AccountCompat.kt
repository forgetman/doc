package compat.account

import android.accounts.Account
import android.content.Context
import compat.account.api.Api
import compat.account.api.Api23Impl
import compat.account.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * [android.accounts.AccountManager]的兼容
 */
object AccountCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.M_23) -> Api23Impl()
        else -> ApiImpl()
    }

    fun getAccounts(context: Context): Array<out Account> = api.getAccounts(context)
}