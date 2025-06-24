package vector.widget.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import vector.os.weak.WeakHandler

/**
 * 自动滚动的ViewPager
 */
@Suppress("unused")
open class ViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ScrollableViewPager(context, attrs) {

    companion object {
        const val DEFAULT_INTERVAL = 4000L

        const val LEFT = 0
        const val RIGHT = 1

        /**
         * do nothing when sliding at the last or first item
         */
        const val SLIDE_BORDER_MODE_NONE = 0

        /**
         * cycle when sliding at the last or first item
         */
        const val SLIDE_BORDER_MODE_CYCLE = 1

        /**
         * deliver event to parent when sliding at the last or first item
         */
        const val SLIDE_BORDER_MODE_TO_PARENT = 2

        const val SCROLL_WHAT = 0
    }

    var interval = DEFAULT_INTERVAL
    var direction = RIGHT
    var isCycle = true
    var isStopScrollWhenTouch = true
    var slideBorderMode = SLIDE_BORDER_MODE_NONE
    private var isBorderAnimation = true

    @Suppress("LeakingThis")
    private val scrollHandler = WeakHandler(this) {
        scrollOnce()
        sendScrollMessage(interval)
    }

    var enableAutoScroll = false
    var isAttachedWindow = false
    private var isAutoScrolling = false

    private var isStopByTouch = false
    private var touchX = 0f
    private var downX = 0f
    private val scroller = SpeedScroller(context, DecelerateInterpolator()).apply {
        val field = ViewPager::class.java.getDeclaredField("mScroller")
        field.isAccessible = true
        field.set(this@ViewPager, this)
    }

    var smoothScroll = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isStopScrollWhenTouch) {
            if (ev.action == MotionEvent.ACTION_DOWN && isAutoScrolling) {
                isStopByTouch = true
                stopAutoScroll()
            } else if ((ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) && isStopByTouch) {
                startAutoScroll()
            }
        }

        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
            touchX = ev.x
            if (ev.action == MotionEvent.ACTION_DOWN) {
                downX = touchX
            }
            val currentItem = currentItem
            val adapter = adapter
            val pageCount = adapter?.count ?: 0
            /**
             * current index is first one and slide to right or current index is
             * last one and slide to left.<br></br>
             * if slide border mode is to parent, then
             * requestDisallowInterceptTouchEvent false.<br></br>
             * else scroll to last one when current item is first one, scroll to
             * first one when current item is last one.
             */
            if (currentItem == 0 && downX <= touchX || currentItem == pageCount - 1 && downX >= touchX) {
                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    if (pageCount > 1) {
                        setCurrentItem(pageCount - currentItem - 1, isBorderAnimation)
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                return super.dispatchTouchEvent(ev)
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isAttachedWindow = true
        if (enableAutoScroll) startAutoScroll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        stopAutoScroll()
    }

    private fun startAutoScroll() {
        if (isAutoScrolling) {
            return
        }
        isAutoScrolling = true
        sendScrollMessage(interval)
    }

    /**
     * start auto scroll
     *
     * @param delayTimeInMills first scroll delay time
     */
    fun startAutoScroll(delayTimeInMills: Int) {
        if (isAutoScrolling) {
            return
        }
        isAutoScrolling = true
        sendScrollMessage(delayTimeInMills.toLong())
    }

    private fun stopAutoScroll() {
        if (!isAutoScrolling) {
            return
        }
        isAutoScrolling = false
        scrollHandler.removeMessages(SCROLL_WHAT)
    }

    /**
     * set the factor by which the duration of sliding animation will change
     */
    fun setScrollDuration(duration: Int) {
        scroller.duration = duration
    }

    private fun sendScrollMessage(delayTimeInMills: Long) {
        /** remove messages before, keeps one message is running at most  */
        scrollHandler.removeMessages(SCROLL_WHAT)
        scrollHandler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills)
    }

    /**
     * scroll only once
     */
    private fun scrollOnce() {
        val adapter = adapter
        var currentItem = currentItem
        val totalCount = adapter?.count ?: 0
        if (adapter == null || totalCount <= 1) {
            return
        }

        val nextItem = if (direction == LEFT) --currentItem else ++currentItem
        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, isBorderAnimation)
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, isBorderAnimation)
            }
        } else {
            setCurrentItem(nextItem, true)
        }
    }

    fun setAutoScroll(enable: Boolean) {
        enableAutoScroll = enable
        if (enable && isAttachedWindow) startAutoScroll()
    }

    /**
     * 可设置滚动速度的scroller
     */
    class SpeedScroller(
        context: Context, interpolator: Interpolator? = null
    ) : Scroller(context, interpolator) {
        private var fixedDuration = 600 // Duration of the scroll in milliseconds

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, fixedDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, fixedDuration)
        }

        fun setDuration(d: Int) {
            fixedDuration = d
        }
    }
}

