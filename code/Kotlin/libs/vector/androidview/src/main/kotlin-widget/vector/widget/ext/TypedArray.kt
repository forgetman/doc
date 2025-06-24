package vector.widget.ext

import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import vector.app.androidview.R
import vector.util.WRAP_CONTENT


fun TypedArray.obtainLayoutDimension(@StyleableRes index: Int, action: (value: Int) -> Unit) {
    if (hasValue(index)) {
        action(getLayoutDimension(index, WRAP_CONTENT))
    }
}

fun TypedArray.obtainInt(@StyleableRes index: Int, action: (value: Int) -> Unit) {
    if (hasValue(index)) {
        action(getInt(index, 0))
    }
}

fun TypedArray.obtainFloat(@StyleableRes index: Int, action: (value: Float) -> Unit) {
    if (hasValue(index)) {
        action(getFloat(index, 0f))
    }
}

fun TypedArray.obtainDrawable(
    @StyleableRes index: Int,
    theme: Theme?,
    action: (drawable: Drawable) -> Unit
) {
    if (hasValue(index)) {
        val id = getResourceId(index, 0)
        if (id == 0) return
        val value = ResourcesCompat.getDrawable(resources, id, findColorTheme() ?: theme) ?: return
        action(value)
    }
}

fun TypedArray.obtainColorStateList(
    @StyleableRes index: Int,
    theme: Theme?,
    action: (value: ColorStateList) -> Unit
) {
    if (hasValue(index)) {
        val id = getResourceId(index, 0)
        if (id == 0) return
        val value = ResourcesCompat.getColorStateList(resources, id, findColorTheme() ?: theme) ?: return
        action(value)
    }
}

fun TypedArray.obtainDimension(
    @StyleableRes index: Int,
    action: (value: Float) -> Unit
) {
    if (hasValue(index)) {
        action(getDimension(index, 0f))
    }
}

fun TypedArray.obtainDimensionPixelSize(
    @StyleableRes index: Int,
    action: (value: Int) -> Unit
) {
    if (hasValue(index)) {
        action(getDimensionPixelSize(index, 0))
    }
}

private fun TypedArray.findColorTheme(): Theme? {
    if (hasValue(R.styleable.LibsVectorCoreRedeclare_View_android_theme)) {
        val themeId = getResourceId(R.styleable.LibsVectorCoreRedeclare_View_android_theme, -1)
        if (themeId == -1) return null
        val colorTheme = resources.newTheme().apply {
            applyStyle(themeId, true)
        }
        return colorTheme
    }
    return null
}