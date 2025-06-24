package compat.inputmethod

import android.content.Context
import android.os.IBinder
import android.view.View
import compat.inputmethod.api.Api
import compat.inputmethod.api.Api31Impl
import compat.inputmethod.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

object InputMethodCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.S_31) -> Api31Impl()
        else -> ApiImpl()
    }

    fun isActive(context: Context, view: View): Boolean {
        return api.isActive(context, view)
    }

    fun showSoftInput(context: Context, view: View, flags: Int): Boolean {
        return api.showSoftInput(context, view, flags)
    }

    fun hideSoftInputFromWindow(context: Context, windowToken: IBinder, flags: Int) {
        api.hideSoftInputFromWindow(context, windowToken, flags)
    }

    fun toggleSoftInput(context: Context, view: View, showFlags: Int, hideFlags: Int) {
        api.toggleSoftInput(context, view, showFlags, hideFlags)
    }
}