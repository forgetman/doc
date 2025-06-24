package compat.account.api

import android.Manifest
import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import android.provider.CalendarContract
import compat.ext.account
import compat.ext.checkPermission

@SuppressLint("MissingPermission")
internal class ApiImpl : Api {

    override fun getAccounts(context: Context): Array<out Account> {
        if (!context.checkPermission(Manifest.permission.GET_ACCOUNTS)) return emptyArray()
        return context.account().getAccountsByType(CalendarContract.ACCOUNT_TYPE_LOCAL)
    }
}