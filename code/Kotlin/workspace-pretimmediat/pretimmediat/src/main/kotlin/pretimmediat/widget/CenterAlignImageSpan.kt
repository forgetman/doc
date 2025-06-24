package pretimmediat.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ImageSpan
import androidx.core.graphics.withTranslation

/**
 * 居中对齐ImageSpan
 */
class CenterAlignImageSpan(context: Context, resourceId: Int) : ImageSpan(context, resourceId) {

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b = drawable
        val fm = paint.fontMetricsInt
        val transY = (y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2 //计算y方向的位移
        canvas.withTranslation(x, transY.toFloat()) {
            b.draw(canvas)
        }
    }
}