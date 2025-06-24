package sugar.ext

import android.graphics.RectF
import android.view.MotionEvent

fun RectF.containsX(x: Float) = x >= left && x < right

fun RectF.containsY(y: Float) = y >= top && y < bottom

fun RectF.contains(ev: MotionEvent?): Boolean {
    if (ev == null) return false
    return contains(ev.x, ev.y)
}