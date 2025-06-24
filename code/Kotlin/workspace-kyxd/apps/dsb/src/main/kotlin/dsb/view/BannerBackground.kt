package dsb.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import vector.ext.setAntialias
import vector.app.os.dp

/**
 * @author yuansui
 * @since 2020/6/26
 */
class BannerBackground @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val ARC_HEIGHT_DEF = 30
    }

    private val path = Path()

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null) return

        canvas.setAntialias()

        canvas.save()
        canvas.clipPath(path)

        super.dispatchDraw(canvas)

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()

        path.reset()

        val arcHeight = ARC_HEIGHT_DEF.dp.toPx(context)
        val drawHeight = h - arcHeight

        path.moveTo(0f, 0f)
        path.addRect(0f, 0f, w, drawHeight, Path.Direction.CCW)

        path.moveTo(0f, drawHeight)
        path.quadTo(w / 2f, h + 50f, w, drawHeight)
    }
}