package pretimmediat.widget.picker

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import logger.L
import pretimmediat.R
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.os.dp
import vector.widget.ext.obtainFloat
import java.lang.ref.WeakReference
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

/**
 * 网上代码, 随便改改
 */
@SuppressLint("CustomViewStyleable")
class PickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val LOG_TAG = "PickerView"

        /**
         * 自动回滚到中间的速度
         */
        private const val AUTO_SCROLL_SPEED = 10f

        /**
         * 透明度：最小 120，最大 255，极差 135
         */
        private const val TEXT_ALPHA_MIN = 120
        private const val TEXT_ALPHA_RANGE = 135
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val highlightColor = ContextCompat.getColor(context, R.color.text_primary)
    private val normalColor = ContextCompat.getColor(context, R.color.text_tertiary)

    private var halfWidth = 0f
    private var halfHeight = 0f
    private var quarterHeight = 0f
    private var minTextSize = 0f
    private var textSizeRange = 0f
    private var textSpacing = 0f
    private var halfTextSpacing = 0f

    private var scrollDistance = 0f
    private var lastTouchY = 0f
    private var dataList = mutableListOf<String>()
    var selectedIndex: Int = 0
        private set
    private var canScroll1 = true
    private var canScrollLoop = true
    private var onSelectListener: OnSelectListener? = null
    private var scrollAnim: ObjectAnimator? = null
    private var canShowAnim = true

    private val timer = Timer()
    private var timerTask: TimerTask? = null
    private val handler: Handler = ScrollHandler(this)

    private var multiplier = 1f

    /**
     * 选择结果回调接口
     */
    fun interface OnSelectListener {
        fun onSelect(view: View, selectedIndex: Int, selected: String)
    }

    private class ScrollTimerTask(handler: Handler) : TimerTask() {
        private val mWeakHandler = WeakReference(handler)

        override fun run() {
            val handler = mWeakHandler.get() ?: return

            handler.sendEmptyMessage(0)
        }
    }

    private class ScrollHandler(view: PickerView) : Handler(Looper.getMainLooper()) {
        private val weakView = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val view = weakView.get() ?: return

            view.keepScrolling()
        }
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PickerView).apply {
            obtainFloat(R.styleable.PickerView_pv_lineSpacingMultiplier) { value ->
                L.d(LOG_TAG, "multiplier = $value")
                multiplier = value
            }
            recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        halfWidth = measuredWidth / 2f
        val height = measuredHeight
        halfHeight = height / 2f
        quarterHeight = height / 4f
        val maxTextSize = 17.dp.toPx(context)
        minTextSize = maxTextSize * 0.9f
        textSizeRange = maxTextSize - minTextSize

        textPaint.textSize = minTextSize + textSizeRange
        val maxText = dataList.maxBy { it.length }
        val focusLayout = if (isSdkAtLeast(SdkInt.M_23)) {
            StaticLayout.Builder.obtain(maxText, 0, maxText.length, textPaint, measuredWidth)
                .setAlignment(Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(maxText, textPaint, width, Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }
        val lineCount = focusLayout.lineCount
        L.d(LOG_TAG, "line count = $lineCount")

        val baseSpacing = minTextSize * 2.2f * multiplier
        textSpacing = if (lineCount <= 1) {
            baseSpacing
        } else {
            baseSpacing * lineCount / 2f
        }
        halfTextSpacing = baseSpacing / 2f
    }

    override fun onDraw(canvas: Canvas) {
        if (selectedIndex >= dataList.size) {
            return
        }

        // 绘制选中的 text
        drawText(canvas, highlightColor, scrollDistance, dataList[selectedIndex])

        // 绘制选中上方的 text
        for (i in 1..selectedIndex) {
            drawText(
                canvas, normalColor, scrollDistance - i * textSpacing,
                dataList[selectedIndex - i]
            )
        }

        // 绘制选中下方的 text
        val size = dataList.size - selectedIndex
        for (i in 1 until size) {
            drawText(
                canvas, normalColor, scrollDistance + i * textSpacing,
                dataList[selectedIndex + i]
            )
        }
    }

    private fun drawText(canvas: Canvas, textColor: Int, offsetY: Float, text: String) {
        if (text.isEmpty()) {
            return
        }

//        var scale = 1 - (offsetY / quarterHeight).pow(2f)
//        scale = if (scale < 0) 0f else scale
//        paint.textSize = minTextSize + textSizeRange * scale
        paint.textSize = minTextSize + textSizeRange
//        textPaint.textSize = minTextSize + textSizeRange
        paint.color = textColor
//        paint.alpha = TEXT_ALPHA_MIN + (TEXT_ALPHA_RANGE * scale).toInt()

        // text 居中绘制，mHalfHeight + offsetY 是 text 的中心坐标
        val fm = paint.fontMetrics
        val focusLayout = if (isSdkAtLeast(SdkInt.M_23)) {
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .setAlignment(Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }

        val lineCount = focusLayout.lineCount
        val baseline = halfHeight + offsetY - (fm.top + fm.bottom) / 2f
        if (lineCount == 1) {
            canvas.drawText(text, halfWidth, baseline, paint)
        } else {
            for (i in 0 until lineCount) {
                val start = focusLayout.getLineStart(i)
                val end = focusLayout.getLineEnd(i)
                val sub = text.substring(start, end)
                val y =
                    baseline + i * halfTextSpacing - halfTextSpacing / lineCount // 简单计算多行的偏移位置, 实际需求最大只有2行, 目测效果还可以
                canvas.drawText(sub, halfWidth, y, paint)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return canScroll1 && super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                cancelTimerTask()
                lastTouchY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                val offsetY = event.y
                scrollDistance += offsetY - lastTouchY
                if (scrollDistance > halfTextSpacing) {
                    if (!canScrollLoop) {
                        if (selectedIndex == 0) {
                            lastTouchY = offsetY
                            invalidate()
                            return true
                        } else {
                            selectedIndex--
                        }
                    } else {
                        // 往下滑超过离开距离，将末尾元素移到首位
                        moveTailToHead()
                    }
                    scrollDistance -= textSpacing
                } else if (scrollDistance < -halfTextSpacing) {
                    if (!canScrollLoop) {
                        if (selectedIndex == dataList.size - 1) {
                            lastTouchY = offsetY
                            invalidate()
                            return true
                        } else {
                            selectedIndex++
                        }
                    } else {
                        // 往上滑超过离开距离，将首位元素移到末尾
                        moveHeadToTail()
                    }
                    scrollDistance += textSpacing
                }
                lastTouchY = offsetY
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                // 抬起手后 mSelectedIndex 由当前位置滚动到中间选中位置
                if (abs(scrollDistance.toDouble()) < 0.01) {
                    scrollDistance = 0f
                    return true
                }
                cancelTimerTask()
                timerTask = ScrollTimerTask(handler)
                timer.schedule(timerTask, 0, 10)
            }
        }
        return true
    }

    private fun cancelTimerTask() {
        timerTask?.cancel()
        timerTask = null

        timer.purge()
    }

    private fun moveTailToHead() {
        if (!canScrollLoop || dataList.isEmpty()) {
            return
        }

        val tail = dataList[dataList.size - 1]
        dataList.removeAt(dataList.size - 1)
        dataList.add(0, tail)
    }

    private fun moveHeadToTail() {
        if (!canScrollLoop || dataList.isEmpty()) {
            return
        }

        val head = dataList[0]
        dataList.removeAt(0)
        dataList.add(head)
    }

    private fun keepScrolling() {
        if (abs(scrollDistance.toDouble()) < AUTO_SCROLL_SPEED) {
            scrollDistance = 0f
            if (timerTask != null) {
                cancelTimerTask()

                if (onSelectListener != null && selectedIndex < dataList.size) {
                    onSelectListener!!.onSelect(this, selectedIndex, dataList[selectedIndex])
                }
            }
        } else if (scrollDistance > 0) {
            // 向下滚动
            scrollDistance -= AUTO_SCROLL_SPEED
        } else {
            // 向上滚动
            scrollDistance += AUTO_SCROLL_SPEED
        }
        invalidate()
    }

    /**
     * 设置数据
     */
    fun setDataList(list: List<String>) {
        if (list.isEmpty()) {
            return
        }

        dataList = list.toMutableList()
        // 重置 mSelectedIndex，防止溢出
        selectedIndex = 0
        invalidate()
    }

    /**
     * 选择选中项
     */
    fun setSelected(index: Int) {
        if (index >= dataList.size) {
            return
        }

        selectedIndex = index
        if (canScrollLoop) {
            // 可循环滚动时，mSelectedIndex 值固定为 mDataList / 2
            val position = dataList.size / 2 - selectedIndex
            if (position < 0) {
                for (i in 0 until -position) {
                    moveHeadToTail()
                    selectedIndex--
                }
            } else if (position > 0) {
                for (i in 0 until position) {
                    moveTailToHead()
                    selectedIndex++
                }
            }
        }
        invalidate()
    }

    /**
     * 设置选择结果监听
     */
    fun setOnSelectListener(listener: OnSelectListener?) {
        onSelectListener = listener
    }

    /**
     * 是否允许滚动
     */
    fun setCanScroll(canScroll: Boolean) {
        canScroll1 = canScroll
    }

    /**
     * 是否允许循环滚动
     */
    fun setCanScrollLoop(canLoop: Boolean) {
        canScrollLoop = canLoop
    }

    /**
     * 执行滚动动画
     */
    fun startAnim() {
        if (!canShowAnim) {
            return
        }

        if (scrollAnim == null) {
            val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
            val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f)
            scrollAnim =
                ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(200)
        }

        if (!scrollAnim!!.isRunning) {
            scrollAnim!!.start()
        }
    }

    /**
     * 是否允许滚动动画
     */
    fun setCanShowAnim(canShowAnim: Boolean) {
        this.canShowAnim = canShowAnim
    }

    /**
     * 销毁资源
     */
    fun onDestroy() {
        onSelectListener = null
        handler.removeCallbacksAndMessages(null)
        if (scrollAnim != null && scrollAnim!!.isRunning) {
            scrollAnim!!.cancel()
        }
        cancelTimerTask()
        timer.cancel()
    }
}