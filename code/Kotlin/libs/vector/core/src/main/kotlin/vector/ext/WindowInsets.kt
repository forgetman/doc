@file:Suppress("DEPRECATION")

package vector.ext

import androidx.core.view.WindowInsetsCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

val WindowInsetsCompat.systemLeft: Int
    get() = if (isSdkAtLeast(SdkInt.R_30)) {
        getInsets(WindowInsetsCompat.Type.systemBars()).left
    } else {
        systemWindowInsetLeft
    }

val WindowInsetsCompat.systemTop: Int
    get() = if (isSdkAtLeast(SdkInt.R_30)) {
        getInsets(WindowInsetsCompat.Type.systemBars()).top
    } else {
        systemWindowInsetTop
    }

val WindowInsetsCompat.systemRight: Int
    get() = if (isSdkAtLeast(SdkInt.R_30)) {
        getInsets(WindowInsetsCompat.Type.systemBars()).right
    } else {
        systemWindowInsetRight
    }

val WindowInsetsCompat.systemBottom: Int
    get() = if (isSdkAtLeast(SdkInt.R_30)) {
        getInsets(WindowInsetsCompat.Type.systemBars()).bottom
    } else {
        systemWindowInsetBottom
    }