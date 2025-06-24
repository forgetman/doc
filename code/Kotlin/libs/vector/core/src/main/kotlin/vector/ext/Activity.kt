@file:Suppress("unused")

package vector.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.AnimRes
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.delegate.ActivityResultCallback
import vector.util.Launcher

fun Activity.withOpenAnim(@AnimRes enter: Int, @AnimRes exit: Int) {
    if (isSdkAtLeast(SdkInt.U_34)) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, enter, exit)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enter, exit)
    }
}

fun Activity.withCloseAnim(@AnimRes enter: Int, @AnimRes exit: Int) {
    if (isSdkAtLeast(SdkInt.U_34)) {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, enter, exit)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enter, exit)
    }
}

inline fun <reified T : Any> Activity.startForResult(
    extras: Bundle? = null,
    callback: ActivityResultCallback
) {
    startForResult(intentFor<T>(), extras, callback)
}

fun Activity.startForResult(
    intent: Intent,
    extras: Bundle? = null,
    callback: ActivityResultCallback
) {
    Launcher.registerForActivityResult(this, intent, extras) { resultCode: Int, data: Intent? ->
        callback.onActivityResult(resultCode, data)
    }
}
