package vector.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withClip
import androidx.core.view.doOnLayout
import vector.app.ext.setAntialias
import vector.app.os.dp

fun interface OnToggleButtonCheckedChanged {
    fun onChanged(view: View, checked: Boolean)
}

@SuppressLint("CustomViewStyleable")
class ToggleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {
        private const val DURATION = 100L
        private val DEFAULT_COLOR_CHECK = "#377bee".toColorInt()
        private val DEFAULT_COLOR_NO_CHECK = "#999999".toColorInt()
        private const val DEFAULT_EDGE_STROKE_WIDTH_DP = 1
    }

    private val rectPath = Path()
    private val edgePath = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val circleEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private var radius: Int = 0
    private var circleCenterX: Int = 0
    private var circleCenterY: Int = 0

    private var w: Int = 0
    private var h: Int = 0

    var isChecked = false
        private set
    private var isAnimating = false
    private var shouldCallback = true
    private var interpolation: Float = 0f

    var listener: OnToggleButtonCheckedChanged? = null

    private val colorChecked: Int
    private val colorUnchecked: Int

    var edgeStrokeWidth: Int = DEFAULT_EDGE_STROKE_WIDTH_DP.dp.toPx(context)
        set(value) {
            field = value
            circleEdgePaint.strokeWidth = value.toFloat()
            updatePaths() // 更新路径确保宽度生效
            invalidate()
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LibsWidgetToggleButton).apply {
            colorChecked = getColor(R.styleable.LibsWidgetToggleButton_toggle_colorCheck, DEFAULT_COLOR_CHECK)
            colorUnchecked =
                getColor(R.styleable.LibsWidgetToggleButton_toggle_colorUnCheck, DEFAULT_COLOR_NO_CHECK)
            edgeStrokeWidth = getDimensionPixelSize(
                R.styleable.LibsWidgetToggleButton_toggle_edgeStrokeWidth,
                DEFAULT_EDGE_STROKE_WIDTH_DP.dp.toPx(context)
            )
            recycle()
        }
        circleEdgePaint.strokeWidth = edgeStrokeWidth.toFloat()
        setup()
    }

    private fun setup() {
        setOnClickListener(this)
        doOnLayout {
            circleCenterY = h / 2
            radius = circleCenterY
            updatePaths()
        }
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        w = measuredWidth
        h = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        canvas.setAntialias()

        // Draw outer edge
        canvas.drawPath(edgePath, circleEdgePaint)

        // Draw background
        paint.color = colorChecked
        canvas.drawPath(rectPath, paint)

        // Draw animated clipping
        computeCircleCenterX()
        canvas.withClip(circleCenterX, 0, width, height) {
            canvas.scale(
                1f - interpolation, 1f - interpolation,
                w / 2f, circleCenterY.toFloat()
            )
            paint.color = Color.WHITE
            canvas.drawPath(rectPath, paint)
        }

        // Draw toggle circle
        drawToggleCircle(canvas)
    }

    private fun drawToggleCircle(canvas: Canvas) {
        canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), radius.toFloat(), circleEdgePaint)
        paint.color = Color.WHITE
        canvas.drawCircle(
            circleCenterX.toFloat(), circleCenterY.toFloat(),
            radius - circleEdgePaint.strokeWidth, paint
        )
    }

    private fun computeCircleCenterX() {
        circleCenterX = radius + ((w - radius * 2) * interpolation).toInt()
    }

    private fun updatePaths() {
        rectPath.reset()
        edgePath.reset()
        val rectF = RectF(
            edgeStrokeWidth.toFloat(),
            edgeStrokeWidth.toFloat(),
            (w - edgeStrokeWidth).toFloat(),
            (h - edgeStrokeWidth).toFloat()
        )
        rectPath.addRoundRect(rectF, radius.toFloat(), radius.toFloat(), Path.Direction.CCW)
        edgePath.addRoundRect(
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            Path.Direction.CCW
        )
    }

    override fun onClick(view: View) {
        toggle(!isChecked, true, true)
    }

    fun toggle(state: Boolean, shouldCallback: Boolean = true, smooth: Boolean = true) {
        if (isAnimating) return

        isChecked = !state
        this.shouldCallback = shouldCallback
        isAnimating = true

        circleEdgePaint.color = if (state) colorChecked else colorUnchecked
        startAnimation(if (state) 0f to 1f else 1f to 0f, if (smooth) DURATION else 0)
    }

    private fun startAnimation(range: Pair<Float, Float>, duration: Long) {
        ValueAnimator.ofFloat(range.first, range.second).apply {
            interpolator = LinearInterpolator()
            setDuration(duration)
            addUpdateListener(animListener)
            addListener(animEndListener)
            start()
        }
    }

    private val animListener = ValueAnimator.AnimatorUpdateListener { animation ->
        interpolation = animation.animatedValue as Float
        postInvalidateOnAnimation()
    }

    private val animEndListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {}
        override fun onAnimationEnd(animator: Animator) {
            isAnimating = false
            isChecked = !isChecked
            if (shouldCallback) listener?.onChanged(this@ToggleButton, isChecked)
        }

        override fun onAnimationCancel(animator: Animator) {}
        override fun onAnimationRepeat(animator: Animator) {}
    }
}