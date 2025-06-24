package vector.app.ext.view

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

/**
 * 主动回收iv里面的图片资源
 */
fun ImageView.recycle() {
    val d = drawable as? BitmapDrawable? ?: return
    setImageBitmap(null)
    d.bitmap.recycle()
}