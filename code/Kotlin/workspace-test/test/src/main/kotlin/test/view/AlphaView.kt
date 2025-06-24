package test.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import vector.app.os.dp
import vector.ext.blue
import vector.ext.green
import vector.ext.red

/**
 * @author yuansui
 * @since 2020/6/13
 */
class AlphaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currColor: Int = 0
    private var newColor: Int = 0
    private var offset: Float = 0f
    private var scrolling: Boolean = false
    private var paint: Paint = Paint()

    fun onChanged(offset: Float, nextColor: Int) {
        scrolling = true
        this.offset = offset
        this.newColor = nextColor
        invalidate()
    }

    fun setColor(color: Int) {
        currColor = color
        scrolling = false
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val gap = 10.dp.toPx(context).toFloat()
        val height1 = height - gap

//        L.www("height = $height")
//        L.www("height1 = $height1")
//        L.www("gap = $gap")
        if (scrolling) {
            val blendColor = ColorUtils.blendARGB(newColor, currColor, 1 - offset)
            canvas.drawRect(0f, 0f, width.toFloat(), height1, paint.apply {
                color = blendColor
            })

            // 绘制过度区域
            paint.reset()
            val startColor1 = Color.argb(255, blendColor.red, blendColor.green, blendColor.blue)
            val endColor1 = Color.argb(0, blendColor.red, blendColor.green, blendColor.blue)
            val gradient =
                LinearGradient(0f, 0f, 0f, gap, startColor1, endColor1, Shader.TileMode.CLAMP)
            paint.shader = gradient
            canvas.save()
            canvas.translate(0f, height1)
            canvas.drawRect(0f, 0f, width.toFloat(), gap, paint)
            canvas.restore()
        } else {
            canvas.drawRect(0f, 0f, width.toFloat(), height1, Paint().apply {
                color = currColor
            })

            // 绘制过度区域
            val paint = Paint()
            val startColor1 = Color.argb(255, currColor.red, currColor.green, currColor.blue)
            val endColor1 = Color.argb(0, currColor.red, currColor.green, currColor.blue)
            val backGradient1 =
                LinearGradient(0f, 0f, 0f, gap, startColor1, endColor1, Shader.TileMode.CLAMP)
            paint.shader = backGradient1
            canvas.save()
            canvas.translate(0f, height1)
            canvas.drawRect(0f, 0f, width.toFloat(), gap, paint)
            canvas.restore()
        }

        // 绘制阴影
        val paint = Paint()
        val startColor = Color.argb(80, 0, 0, 0)
        val endColor = Color.argb(0, 0, 0, 0)
        val backGradient =
            LinearGradient(0f, 0f, 0f, height1, startColor, endColor, Shader.TileMode.CLAMP)
        paint.shader = backGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height1, paint)
    }

    private fun get(start: Int, end: Int, height: Float): LinearGradient {
        val startColor = Color.argb(255, start.red, start.green, start.blue)
        val endColor = Color.argb(0, end.red, end.green, end.blue)
        return LinearGradient(0f, 0f, 0f, height, startColor, endColor, Shader.TileMode.CLAMP)
    }
}