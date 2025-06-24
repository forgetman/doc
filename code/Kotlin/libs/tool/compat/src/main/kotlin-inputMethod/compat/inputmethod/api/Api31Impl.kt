package compat.inputmethod.api

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import compat.ext.inputMethod

@RequiresApi(Build.VERSION_CODES.S)
class Api31Impl : Api by ApiImpl() {

    override fun isActive(context: Context, view: View): Boolean {
        val isActive = context.inputMethod().isActive(view)
        if (isActive) return true
        val rootInsets = view.rootWindowInsets
        return rootInsets != null && rootInsets.isVisible(WindowInsets.Type.ime())
    }

    /**
     * [android.view.inputmethod.InputMethodManager.toggleSoftInput]
     * method was deprecated in API level 31.
     * Use showSoftInput(android.view.View, int) or hideSoftInputFromWindow(android.os.IBinder, int) explicitly instead.
     * In particular during focus changes, the current visibility of the IME is not well defined.
     * Starting in Android S, this only has an effect if the calling app is the current IME focus.
     */
    override fun toggleSoftInput(context: Context, view: View, showFlags: Int, hideFlags: Int) {
        val isActive = context.inputMethod().isActive(view)
        if (isActive) {
            hideSoftInputFromWindow(context, view.windowToken, hideFlags)
        } else {
            val rootInsets = view.rootWindowInsets
            if (rootInsets != null && rootInsets.isVisible(WindowInsets.Type.ime())) {
                hideSoftInputFromWindow(context, view.windowToken, hideFlags)
            } else {
                showSoftInput(context, view, showFlags)
            }
        }
    }
}