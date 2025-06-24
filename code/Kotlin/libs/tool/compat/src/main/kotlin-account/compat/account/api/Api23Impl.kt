package compat.account.api

import android.Manifest
import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import android.provider.CalendarContract
import compat.ext.account
import compat.ext.checkPermission

internal class Api23Impl : Api by ApiImpl() {

    @SuppressLint("MissingPermission")
    override fun getAccounts(context: Context): Array<out Account> {
        if (!context.checkPermission(Manifest.permission.GET_ACCOUNTS)) return emptyArray()
        return context.account().accounts.filter { it.type == CalendarContract.ACCOUNT_TYPE_LOCAL }.toTypedArray()
    }
}