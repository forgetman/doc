package compat.window.api.insets

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.Window

internal interface Api {
    fun statusBarsTop(window: Window): Int
    fun safeContentTop(window: Window): Int
    fun safeContentBottom(window: Window): Int
}

@SuppressLint("InternalInsetResource", "DiscouragedApi")
internal fun getStatusBarTopLegacy(): Int {
    val id = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
    return if (id > 0) Resources.getSystem().getDimensionPixelSize(id) else 0
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
internal fun getNavigationBarBottomLegacy(): Int {
    val resourceId = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) Resources.getSystem().getDimensionPixelSize(resourceId) else 0
}