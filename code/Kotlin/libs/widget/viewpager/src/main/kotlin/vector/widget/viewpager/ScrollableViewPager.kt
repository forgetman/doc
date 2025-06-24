package vector.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * 可取消滑动的viewpager
 * 解决如下问题
 * 1. ListView嵌套viewpager无法上下滑动冲突的问题
 * 2. viewPager嵌套viewPager. 子和父只能二选一滑动的问题
 * ps: 只支持2级嵌套
 */
open class ScrollableViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    var isScrollable = true

    private var downX: Float = 0f
    private var downY: Float = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        if (id == NO_ID) id = generateViewId()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isScrollable) {
            super.onInterceptTouchEvent(event)
        } else {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isScrollable) {
            super.onTouchEvent(event)
        } else {
            false
        }
    }

    fun isForward(x: Float) = x - downX >= touchSlop

    fun isBackward(x: Float) = x - downX <= -touchSlop

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                disallowIntercept(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val xMove = abs(ev.x - downX)
                val yMove = abs(ev.y - downY)

                if (xMove > yMove) {
                    // 如果只有一个item
                    val count = adapter?.count ?: 0
                    if (count == 1 || count == 0) {
                        disallowIntercept(false)
                        return super.dispatchTouchEvent(ev)
                    }

                    when (currentItem) {
                        0 -> {
                            // 判断方向
                            if (isForward(ev.x)) {
                                disallowIntercept(false)
                            } else {
                                disallowIntercept(true)
                            }
                        }

                        count.minus(1) -> {
                            // 判断方向
                            if (isBackward(ev.x)) {
                                disallowIntercept(false)
                            } else {
                                disallowIntercept(true)
                            }
                        }

                        else -> {
                            if (xMove >= touchSlop) disallowIntercept(true)
                        }
                    }
                } else {
                    if (yMove >= touchSlop) disallowIntercept(false)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> disallowIntercept(false)
            else -> Unit
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun disallowIntercept(disallowIntercept: Boolean) {
        parent.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }
}
