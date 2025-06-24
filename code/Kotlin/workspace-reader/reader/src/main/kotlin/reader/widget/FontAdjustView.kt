package reader.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import reader.R
import vector.app.ext.drawBitmapInXAlign
import vector.app.ext.setAntialias
import vector.app.ext.toBitmap
import vector.app.os.dp
import vector.app.util.toDrawable

internal typealias OnTouchFontSizeListener = (index: Int) -> Unit

class FontAdjustView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val LINE_COUNT = 11
        private val LINE_HEIGHT = 2.6f.dp
        private val SCALE_WIDTH = 2.dp
        private val SCALE_HEIGHT = 7.dp
    }

    private val lineHeight = LINE_HEIGHT.toPx(this).toFloat()
    private val scaleWidth = SCALE_WIDTH.toPx(this).toFloat()
    private val scaleHeight = SCALE_HEIGHT.toPx(this).toFloat()

    var listener: OnTouchFontSizeListener? = null

    private val paintLine = Paint().apply {
        color = "#ececec".toColorInt()
        this.strokeWidth = lineHeight
    }

    private val paintScale = Paint().apply {
        color = "#ececec".toColorInt()
        this.strokeWidth = scaleWidth
    }

    var focusIndex = 0

    private var areaWidth: Float = 0f
    private var midHeight = 0f
    private var widthCanDraw = 0f

    private var thumb = R.drawable.shape_font_indicator.toDrawable(context)?.toBitmap()
    private var radius: Float = (thumb?.width?.toFloat() ?: 0f) / 2f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        widthCanDraw = measuredWidth - radius * 2
        areaWidth = widthCanDraw / (LINE_COUNT - 1)
        midHeight = measuredHeight / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.setAntialias()

        val lineStart = radius
        val lineEnd = widthCanDraw + lineStart
        canvas.drawLine(lineStart, midHeight, lineEnd, midHeight, paintLine)

        for (i in 0 until LINE_COUNT) {
            val areaStartX = i * areaWidth + lineStart

            if (i == focusIndex) {
                canvas.drawBitmapInXAlign(
                    thumb,
                    areaStartX,
                    0f,
                    null,
                    Paint.Align.CENTER
                )
            } else {
                val drawX = areaStartX - scaleWidth / 2
                canvas.drawLine(
                    drawX,
                    midHeight - scaleHeight / 2,
                    drawX,
                    midHeight + scaleHeight / 2,
                    paintScale
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x

        // 算出点击的索引
        val index = (x / areaWidth).toInt()

        // 保存上次点击的字母的索引到oldChoose
        val oldIndex = focusIndex
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (oldIndex != index && index >= 0 && index < LINE_COUNT) {
                    focusIndex = index
                    invalidate()

                    listener?.invoke(index)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (oldIndex != index && index >= 0 && index < LINE_COUNT) {
                    focusIndex = index
                    invalidate()

                    listener?.invoke(index)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }

        return true
    }
}