package compat.inputmethod.api

import android.content.Context
import android.os.IBinder
import android.view.View

interface Api {
    fun isActive(context: Context, view: View): Boolean
    fun showSoftInput(context: Context, view: View, flags: Int): Boolean
    fun hideSoftInputFromWindow(context: Context, windowToken: IBinder, flags: Int)

    /**
     * This method toggles the input method window display.
     *
     * If the input window is already displayed, it gets hidden.
     * If not the input window will be displayed.
     * @param showFlags Provides additional operating flags.  May be
     * 0 or have the [android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT],
     * [android.view.inputmethod.InputMethodManager.SHOW_FORCED] bit set.
     * @param hideFlags Provides additional operating flags.  May be
     * 0 or have the [android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY],
     * [android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS] bit set.
     */
    fun toggleSoftInput(context: Context, view: View, showFlags: Int, hideFlags: Int)
}