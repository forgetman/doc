package compat.account.api

import android.accounts.Account
import android.content.Context

/**
 * @author yuansui
 * @since 2023/7/28
 */
internal interface Api {

    fun getAccounts(context: Context): Array<out Account>
}