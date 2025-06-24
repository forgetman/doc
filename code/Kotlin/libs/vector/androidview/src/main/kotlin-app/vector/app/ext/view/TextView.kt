@file:Suppress("unused")

package vector.app.ext.view

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import vector.app.util.Res

fun TextView.setDrawableIdsWithIntrinsicBounds(
    @DrawableRes left: Int? = 0,
    @DrawableRes top: Int? = 0,
    @DrawableRes right: Int? = 0,
    @DrawableRes bottom: Int? = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0)
}

fun TextView.setDrawablesWithIntrinsicBounds(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null,
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

private fun getDrawable(context: Context, id: Int?): Drawable? {
    if (id == null || id == 0) return null
    return Res.getDrawable(context, id)
}

/**
 * 加入下划线
 */
fun TextView.setUnderLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
}

/**
 * 恢复初始状态
 */
fun TextView.resetFlag() {
    paint.flags = 0
}

/**
 * 限制输入字数
 */
fun TextView.limitInputCount(max: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
}

fun View.setTextByFindId(@IdRes tvResId: Int, action: () -> CharSequence?) {
    this.findViewById<TextView>(tvResId)?.text = action()
}