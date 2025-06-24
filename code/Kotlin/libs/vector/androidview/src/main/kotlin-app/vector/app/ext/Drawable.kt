@file:Suppress("unused")

package vector.app.ext

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

fun Drawable.toBitmap(
    width: Int? = null,
    height: Int? = null,
    config: Bitmap.Config? = null
): Bitmap {
    if (this is BitmapDrawable) {
        val b = this.bitmap
        if (b != null) return b
    }

    val w: Int = width ?: this.width.takeIf { it > 0 } ?: 512
    val h: Int = height ?: this.height.takeIf { it > 0 } ?: 512

    return Bitmap.createBitmap(w, h, config ?: Bitmap.Config.ARGB_8888)
        .applyCanvas {
            val (oldLeft, oldTop, oldRight, oldBottom) = bounds
            setBounds(0, 0, w, h)
            draw(this)
            setBounds(oldLeft, oldTop, oldRight, oldBottom)
        }
}

fun Drawable.setIntrinsicBounds(width: Int? = null, height: Int? = null) {
    // 判断bounds是否无效, 如果已经提前设置了, 忽略
    if (bounds.isEmpty) {
        setBounds(0, 0, width ?: intrinsicWidth, height ?: intrinsicHeight)
    }
}

val Drawable.width: Int
    get() = (this as? BitmapDrawable)?.bitmap?.width ?: intrinsicWidth

val Drawable.height: Int
    get() = (this as? BitmapDrawable)?.bitmap?.height ?: intrinsicHeight

val Drawable.isVector: Boolean
    get() = (this is VectorDrawableCompat) || (Build.VERSION.SDK_INT > 21 && this is VectorDrawable)