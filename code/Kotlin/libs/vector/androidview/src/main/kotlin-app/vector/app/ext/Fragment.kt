@file:Suppress("unused")

package vector.app.ext

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import vector.app.delegate.ActivityResultCallback
import vector.ext.getStringForLanguage
import vector.ext.intentFor
import vector.app.os.Dimension
import vector.app.os.DimensionSize
import vector.util.AdditionalOptions
import vector.util.Launcher

/******************
 * [inflate] / [inflateSpace]
 * 为了[androidx.fragment.app.Fragment]里面使用的时候不需要调用[androidx.fragment.app.Fragment.requireContext]
 *************/

fun Fragment.inflate(res: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false) =
    requireContext().inflate(res, parent, attachToRoot)

fun Fragment.inflateSpace(height: Dimension) =
    requireContext().inflateSpace(height)

fun Fragment.inflateSpace(size: DimensionSize) =
    requireContext().inflateSpace(size)

inline fun <reified T : Any> Fragment.startActivity(
    extras: Bundle? = null,
    options: AdditionalOptions? = null
) {
    Launcher.startActivity(this, T::class, extras, options)
}

fun Fragment.startActivity(
    intent: Intent,
    extras: Bundle? = null,
    options: AdditionalOptions? = null
) {
    Launcher.startActivity(this, intent, extras, options)
}

inline fun <reified T : Any> Fragment.startForResult(
    extras: Bundle? = null,
    callback: ActivityResultCallback
) {
    startForResult(requireContext().intentFor<T>(), extras, callback)
}

fun Fragment.startForResult(
    intent: Intent,
    extras: Bundle? = null,
    callback: ActivityResultCallback
) {
    Launcher.registerForActivityResult(this, intent, extras) { resultCode: Int, data: Intent? ->
        callback.onActivityResult(resultCode, data)
    }
}

fun Fragment.getStringForLanguage(@StringRes resId: Int) = requireContext().getStringForLanguage(resId)

fun Fragment.getStringForLanguage(@StringRes resId: Int, vararg formatArgs: Any) =
    requireContext().getStringForLanguage(resId, formatArgs)