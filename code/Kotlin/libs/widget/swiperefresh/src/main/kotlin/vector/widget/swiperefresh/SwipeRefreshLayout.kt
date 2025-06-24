package vector.widget.swiperefresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.ExpandableListView
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import vector.app.config.Config
import vector.app.os.dp
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.widget.swiperefresh.delegate.SwipeRefresh
import vector.widget.swiperefresh.header.BaseSwipeHeader
import vector.widget.swiperefresh.header.DefaultSwipeHeader
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 下拉刷新的外部layout, 根据网上代码更改
 */
class SwipeRefreshLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_DRAG_RATE = 1

        val DRAG_DISTANCE_MAX = 60.dp
        const val DRAG_RATE = .5f
        const val FACTOR = 2f
        const val MAX_ANIM_DURATION = 700
        const val INVALID_POINTER = -1
    }

    lateinit var contentView: View

    private val interpolator: Interpolator = DecelerateInterpolator(FACTOR)
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var totalDragDistance: Int = 0

    lateinit var swipeHeader: BaseSwipeHeader

    private var currentDragPercent: Float = 0f
    private var currentOffsetTop: Int = 0

    /**
     * 是否正在下拉刷新
     */
    var isRefreshing: Boolean = false
        private set

    private var activePointerId: Int = 0
    private var isBeingDragged: Boolean = false
    private var initialMotionY: Float = 0f
    private var from: Int = 0
    private var fromDragPercent: Float = 0f
    private var notify: Boolean = false

    var listener: SwipeRefresh.Listener? = null
    private var isAnimating = false

    private val animateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    private val animateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int = from + ((totalDragDistance - from) * interpolatedTime).toInt()
            val offset = targetTop - contentView.top

            currentDragPercent = fromDragPercent - (fromDragPercent - 1.0f) * interpolatedTime
            swipeHeader.setPercent(currentDragPercent, false)

            setTargetOffsetTop(offset, false /* requires update */)
        }
    }

    private val toStartListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            swipeHeader.changeUiStyle(SwipeRefresh.UiState.IDLE)
            currentOffsetTop = contentView.top
            isAnimating = false
        }
    }

    init {
        setWillNotDraw(true)
        isChildrenDrawingOrderEnabled = true

        totalDragDistance = if (isInEditMode) {
            DRAG_DISTANCE_MAX.toPx(context)
        } else {
            (Config.list().dragDistance ?: DRAG_DISTANCE_MAX).toPx(context)
        }
    }

    /**
     * This method sets padding for the refresh (progress) view.
     */
    fun setRefreshViewPadding(start: Int, top: Int, end: Int, bottom: Int) {
        swipeHeader.setPadding(start, top, end, bottom)
    }

    private fun setHeader() {
        if (this::swipeHeader.isInitialized) return

        swipeHeader = if (isInEditMode) {
            DefaultSwipeHeader(context)
        } else {
            SwipeRefreshConfig.swipeHeaderConstructor?.invoke(context) ?: DefaultSwipeHeader(context)
        }
        addView(swipeHeader, 0, LayoutParamsFactory.linear(MATCH_PARENT, WRAP_CONTENT))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setHeader()
        swipeHeader.measure(widthMeasureSpec, heightMeasureSpec)

        contentView = getChildAt(1)

        if (contentView !is RecyclerView && contentView !is ExpandableListView) {
            throw IllegalStateException("can not find RecyclerView or ExpandableListView")
        }

        contentView.measure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || isRefreshing || isAnimating || canChildScrollUp()) {
            return false
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTop(0, true)
                activePointerId = ev.getPointerId(0)
                isBeingDragged = false
                val initialMotionY = getMotionEventY(ev, activePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                this.initialMotionY = initialMotionY
            }

            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(ev, activePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - initialMotionY
                if (yDiff > touchSlop && !isBeingDragged) {
                    isBeingDragged = true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isBeingDragged = false
                activePointerId = INVALID_POINTER
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(ev)
            }
        }

        return isBeingDragged
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (!isBeingDragged) {
            return super.onTouchEvent(ev)
        }

        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val y = ev.getY(pointerIndex)
                val yDiff = y - initialMotionY
                val scrollTop = yDiff * DRAG_RATE
                currentDragPercent = scrollTop / totalDragDistance
                if (currentDragPercent < 0) {
                    return false
                }
                val boundedDragPercent = min(1f, abs(currentDragPercent))
                val extraOS = abs(scrollTop) - totalDragDistance
                val slingshotDist = totalDragDistance.toFloat()
                val tensionSlingshotPercent =
                    max(0f, min(extraOS, slingshotDist * 2) / slingshotDist)

                val tensionQuarterOne = tensionSlingshotPercent / 4
                val tensionPercent =
                    (tensionQuarterOne - tensionQuarterOne.toDouble().pow(2.0)).toFloat() * 2f

                val extraMove = slingshotDist * tensionPercent / 2
                val targetY = (slingshotDist * boundedDragPercent + extraMove).toInt()

                swipeHeader.setPercent(currentDragPercent, true)
                if (currentDragPercent >= MAX_DRAG_RATE) {
                    swipeHeader.changeUiStyle(SwipeRefresh.UiState.READY)
                } else {
                    swipeHeader.changeUiStyle(SwipeRefresh.UiState.IDLE)
                }

                setTargetOffsetTop(targetY - currentOffsetTop, true)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                activePointerId = ev.getPointerId(index)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }
                val pointerIndex = ev.findPointerIndex(activePointerId)
                val y = ev.getY(pointerIndex)
                val overScrollTop = (y - initialMotionY) * DRAG_RATE
                isBeingDragged = false
                if (overScrollTop > totalDragDistance) {
                    setRefreshing(refreshing = true, notify = true)
                } else {
                    isRefreshing = false
                    animateOffsetToStartPosition()
                }
                activePointerId = INVALID_POINTER
                return false
            }
        }

        return true
    }

    private fun animateOffsetToStartPosition() {
        isAnimating = true

        from = currentOffsetTop
        fromDragPercent = currentDragPercent
        val animationDuration = abs((MAX_ANIM_DURATION * fromDragPercent).toLong())

        animateToStartPosition.reset()
        animateToStartPosition.duration = animationDuration
        animateToStartPosition.interpolator = interpolator
        animateToStartPosition.setAnimationListener(toStartListener)
        swipeHeader.clearAnimation()
        swipeHeader.startAnimation(animateToStartPosition)
    }

    private fun animateOffsetToCorrectPosition() {
        isAnimating = true

        from = currentOffsetTop
        fromDragPercent = currentDragPercent

        animateToCorrectPosition.reset()
        animateToCorrectPosition.duration = MAX_ANIM_DURATION.toLong()
        animateToCorrectPosition.interpolator = interpolator
        swipeHeader.clearAnimation()
        swipeHeader.startAnimation(animateToCorrectPosition)

        if (isRefreshing) {
            swipeHeader.changeUiStyle(SwipeRefresh.UiState.LOADING)
            if (notify) {
                listener?.onSwipeStateChanged(SwipeRefresh.State.START)
            }
        } else {
            swipeHeader.changeUiStyle(SwipeRefresh.UiState.IDLE)
            animateOffsetToStartPosition()
        }
        currentOffsetTop = contentView.top
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = from - (from * interpolatedTime).toInt()
        val targetPercent = fromDragPercent * (1.0f - interpolatedTime)
        val offset = targetTop - contentView.top

        currentDragPercent = targetPercent
        swipeHeader.setPercent(currentDragPercent, true)
        setTargetOffsetTop(offset, false)
    }

    fun setRefreshing(refreshing: Boolean) {
        if (isRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (!this::swipeHeader.isInitialized) return

        if (isRefreshing != refreshing) {
            this.notify = notify
            isRefreshing = refreshing
            if (isRefreshing) {
                animateOffsetToCorrectPosition()
            } else {
                animateOffsetToStartPosition()
                if (notify) {
                    listener?.onSwipeStateChanged(SwipeRefresh.State.END)
                }
            }
        }
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            activePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        return if (index < 0) {
            -1f
        } else ev.getY(index)
    }

    private fun setTargetOffsetTop(offset: Int, requiresUpdate: Boolean) {
        ViewCompat.offsetTopAndBottom(contentView, offset)
        swipeHeader.offset(offset)
        currentOffsetTop = contentView.top
        if (requiresUpdate) {
            invalidate()
        }
    }

    private fun canChildScrollUp(): Boolean {
        return contentView.canScrollVertically(-1)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val height = measuredHeight
        val width = measuredWidth
        val start = paddingStart
        val top = paddingTop
        val end = paddingEnd
        val bottom = paddingBottom

        swipeHeader.layout(start, top, start + width - end, top + height - bottom)
        contentView.layout(
            start,
            top + currentOffsetTop,
            start + width - end,
            top + height - bottom + currentOffsetTop
        )
    }

    /**
     * 自动下拉刷新
     */
    fun startRefresh() {
        setRefreshing(refreshing = true, notify = true)
    }

    /**
     * 停止刷新
     */
    fun stopRefresh() {
        setRefreshing(refreshing = false, notify = true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    private fun reset() {
        from = currentOffsetTop
        fromDragPercent = currentDragPercent

        animateToStartPosition.reset()
        swipeHeader.clearAnimation()

        isAnimating = false
        isRefreshing = false

        moveToStart(1f)

        listener?.onSwipeStateChanged(SwipeRefresh.State.END)
    }
}

