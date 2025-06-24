@file:Suppress("unused")

package vector.app.ext.view

import android.view.View
import android.view.ViewGroup

fun ViewGroup.forEach(action: (View) -> Unit) {
    for (i in 0 until childCount) {
        action(getChildAt(i))
    }
}

fun ViewGroup.forEachIndex(action: (index: Int, v: View) -> Unit) {
    for (i in 0 until childCount) {
        action(i, getChildAt(i))
    }
}