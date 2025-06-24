package reader.widget

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import reader.EventId
import reader.R
import reader.sendMessage
import reader.util.PageDrawer
import vector.app.os.dimenRes
import vector.widget.viewpager2.ViewPager2
import kotlin.math.abs

/**
 * @author yuansui
 * @since 2019/11/9
 */
class MeasuredViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager2(context, attrs) {

    private val point = PointF()

    private var midRect = RectF()

    private var moved = false
    private var touchSlop: Int = 0

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop

        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val w = width
            val h = height

            val midWidth = w / 2

            val distance: Float = (midWidth / 3).toFloat()
            midRect.set(midWidth - distance, 0f, midWidth + distance, h.toFloat())

            val margin = R.dimen.margin_level_1.dimenRes.toPx(context)
            PageDrawer.initSize(w - margin * 2, h)
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                point.set(e.x, e.y)
                moved = false
            }

            MotionEvent.ACTION_MOVE -> {
                val xDiff = abs(point.x - e.x)
                val yDiff = abs(point.y - e.y)
                moved = xDiff + yDiff > touchSlop
            }

            MotionEvent.ACTION_UP -> {
                if (moved) return super.onInterceptTouchEvent(e)

                // 判断点击区域, 顺序不能改变
                when {
                    midRect.contains(point.x, point.y) -> sendMessage(EventId.TOUCH_AREA_CENTER)
                    point.x < midRect.left -> sendMessage(EventId.TOUCH_AREA_LEFT)
                    point.x > midRect.right -> sendMessage(EventId.TOUCH_AREA_RIGHT)
                }
            }
        }

        return super.onInterceptTouchEvent(e)
    }
}