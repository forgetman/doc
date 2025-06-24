package compat.inputmethod.api

import android.content.Context
import android.os.IBinder
import android.view.View
import compat.ext.inputMethod

@Suppress("DEPRECATION")
internal class ApiImpl : Api {

    override fun isActive(
        context: Context,
        view: View
    ): Boolean {
        return context.inputMethod().isActive(view)
    }

    override fun showSoftInput(
        context: Context,
        view: View,
        flags: Int
    ): Boolean {
        return context.inputMethod().showSoftInput(view, flags)
    }

    override fun hideSoftInputFromWindow(
        context: Context,
        windowToken: IBinder,
        flags: Int
    ) {
        context.inputMethod().hideSoftInputFromWindow(windowToken, flags)
    }

    override fun toggleSoftInput(
        context: Context,
        view: View,
        showFlags: Int,
        hideFlags: Int
    ) {
        context.inputMethod().toggleSoftInput(showFlags, hideFlags)
    }

}