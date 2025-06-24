@file:Suppress("unused")

package vector.app.ext

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import vector.ext.DayNightMode
import vector.app.fitter.FitResources

fun setDayNightMode(mode: DayNightMode) {
    val appMode: Int = when (mode) {
        DayNightMode.DAY -> {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        DayNightMode.NIGHT -> {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        DayNightMode.FOLLOW_SYSTEM -> {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
    AppCompatDelegate.setDefaultNightMode(appMode)

    // 切换之后需要清空适配
    FitResources.clear()
}

internal const val NO_GETTER: String = "Property does not have a getter"
internal const val NO_SETTER: String = "Property does not have a setter"
internal fun noGetter(): Nothing = throw Exception("Property does not have a getter")
internal fun noSetter(): Nothing = throw Exception("Property does not have a setter")

fun LayoutInflater.inflate(@LayoutRes id: Int): View = inflate(id, null)