package lib.base.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

object WidgetUtil {

    /**
     * 缩放画在最终的Canvas上
     *
     * @param c
     */
    fun drawScale(c: Canvas, width: Float, height: Float, drawBmp: Bitmap, m: Matrix) {
        val scaleW = width / drawBmp.width.toFloat()
        val scaleH = height / drawBmp.height.toFloat()
        val scaleFinal = if (scaleW < scaleH) scaleW else scaleH // 按照小的比例来决定缩放比例

        val marginLeft = (width - scaleFinal * drawBmp.width) / 2
        val marginTop = (height - scaleFinal * drawBmp.height) / 2
        m.reset()
        m.preScale(scaleFinal, scaleFinal)
        m.postTranslate(marginLeft, marginTop)
        c.drawBitmap(drawBmp, m, null)
    }
}
