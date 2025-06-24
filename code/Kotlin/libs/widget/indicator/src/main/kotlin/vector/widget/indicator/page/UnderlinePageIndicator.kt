/*
 * Copyright (C) 2012 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vector.widget.indicator.page

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import vector.app.util.Res.getColor
import vector.widget.indicator.R

/**
 * Draws a line for each page. The current KPage line is colored differently
 * than the unselected page lines.
 */
class UnderlinePageIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.vpiUnderlinePageIndicatorStyle
) : View(context, attrs, defStyle), PageIndicator {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mFades = false
    var fadeDelay = 0
    private var mFadeLength = 0
    private var mFadeBy = 0
    private var mViewPager: ViewPager? = null
    private var mScrollState = 0
    private var mCurrentPage = 0
    private var mPositionOffset = 0f
    private val mTouchSlop: Int
    private var mLastMotionX = -1f
    private var mActivePointerId = INVALID_POINTER
    private var mIsDragging = false
    private var mLineWidth = 0
    private val mFadeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!mFades) {
                return
            }
            val alpha = Math.max(mPaint.alpha - mFadeBy, 0)
            //            mPaint.setAlpha(alpha);
            invalidate()
            if (alpha > 0) {
                postDelayed(this, FADE_FRAME_MS.toLong())
            }
        }
    }
    var fades: Boolean
        get() = mFades
        set(fades) {
            if (fades != mFades) {
                mFades = fades
                if (fades) {
                    post(mFadeRunnable)
                } else {
                    removeCallbacks(mFadeRunnable)
                    mPaint.alpha = 0xFF
                    invalidate()
                }
            }
        }
    var fadeLength: Int
        get() = mFadeLength
        set(fadeLength) {
            mFadeLength = fadeLength
            mFadeBy = 0xFF / (mFadeLength / FADE_FRAME_MS)
        }
    var selectedColor: Int
        get() = mPaint.color
        set(selectedColor) {
            mPaint.color = selectedColor
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mViewPager == null || mViewPager!!.adapter == null) {
            return
        }
        val count = mViewPager!!.adapter!!.count
        if (count == 0) {
            return
        }
        if (mCurrentPage >= count) {
            setCurrentItem(count - 1)
            return
        }
        val paddingStart = paddingLeft
        val pageWidth = (width - paddingStart - paddingRight) / (1f * count)

        // 如果有设定的宽度, 需要计算偏移量
        var offset = 0f
        if (mLineWidth != 0 && mLineWidth < pageWidth) {
            offset = (pageWidth - mLineWidth) / 2
        }
        if (java.lang.Float.isNaN(mPositionOffset)) {
            mPositionOffset = 0f
        }
        val left = paddingStart + pageWidth * (mCurrentPage + mPositionOffset) + offset
        val right = left + pageWidth - offset * 2
        val top = paddingTop.toFloat()
        val bottom = (height - paddingBottom).toFloat()
        canvas.drawRect(left, top, right, bottom, mPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (super.onTouchEvent(ev)) {
            return true
        }
        if (mViewPager == null) {
            return false
        }
        val adapter = mViewPager!!.adapter
        if (adapter == null || adapter.count == 0) {
            return false
        }
        val action = ev.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mLastMotionX = ev.x
            }

            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                val x = ev.getX(activePointerIndex)
                val deltaX = x - mLastMotionX
                if (!mIsDragging) {
                    if (Math.abs(deltaX) > mTouchSlop) {
                        mIsDragging = true
                    }
                }
                if (mIsDragging) {
                    mLastMotionX = x
                    if (mViewPager!!.isFakeDragging || mViewPager!!.beginFakeDrag()) {
                        mViewPager!!.fakeDragBy(deltaX)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!mIsDragging) {
                    val count = mViewPager!!.adapter!!.count
                    val width = width
                    val halfWidth = width / 2f
                    val sixthWidth = width / 6f
                    if (mCurrentPage > 0 && ev.x < halfWidth - sixthWidth) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager!!.currentItem = mCurrentPage - 1
                        }
                        return true
                    } else if (mCurrentPage < count - 1 && ev.x > halfWidth + sixthWidth) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager!!.currentItem = mCurrentPage + 1
                        }
                        return true
                    }
                }
                mIsDragging = false
                mActivePointerId = INVALID_POINTER
                if (mViewPager!!.isFakeDragging) {
                    mViewPager!!.endFakeDrag()
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                mLastMotionX = ev.getX(index)
                mActivePointerId = ev.getPointerId(index)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId))
            }
        }
        return true
    }

    override fun setViewPager(viewPager: ViewPager) {
        if (mViewPager === viewPager) {
            return
        }
        mViewPager?.removeOnPageChangeListener(this)
        checkNotNull(viewPager.adapter) { "ViewPager does not have adapter instance." }
        mViewPager = viewPager
        viewPager.addOnPageChangeListener(this)
        invalidate()
        post {
            if (mFades) {
                post(mFadeRunnable)
            }
        }
    }

    override fun setViewPager(viewPager: ViewPager, initialPosition: Int) {
        setViewPager(viewPager)
        setCurrentItem(initialPosition)
    }

    override fun setCurrentItem(item: Int) {
        checkNotNull(mViewPager) { "ViewPager has not been bound." }
        mViewPager!!.currentItem = item
        mCurrentPage = item
        invalidate()
    }

    override fun notifyDataSetChanged() {
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
        mScrollState = state
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mCurrentPage = position
        mPositionOffset = positionOffset
        if (mFades) {
            if (positionOffsetPixels > 0) {
                removeCallbacks(mFadeRunnable)
                mPaint.alpha = 0xFF
            } else if (mScrollState != ViewPager.SCROLL_STATE_DRAGGING) {
                postDelayed(mFadeRunnable, fadeDelay.toLong())
            }
        }
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position
            mPositionOffset = 0f
            invalidate()
            mFadeRunnable.run()
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mCurrentPage = savedState.currentPage
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentPage = mCurrentPage
        return savedState
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mViewPager != null) {
            mViewPager!!.clearOnPageChangeListeners()
            mViewPager = null
        }
        removeCallbacks(mFadeRunnable)
    }

    internal class SavedState : BaseSavedState {
        var currentPage = 0

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            currentPage = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPage)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    /**
     * 设置固定的线宽
     *
     * @param width 线宽
     */
    fun setLineWidth(width: Int) {
        mLineWidth = width
    }

    companion object {
        private const val INVALID_POINTER = -1
        private const val FADE_FRAME_MS = 30
    }

    init {
        val res = resources

        // Load defaults from resources
        val defaultFades = res.getBoolean(R.bool.default_underline_indicator_fades)
        val defaultFadeDelay = res.getInteger(R.integer.default_underline_indicator_fade_delay)
        val defaultFadeLength = res.getInteger(R.integer.default_underline_indicator_fade_length)
        val defaultSelectedColor =
            getColor(context, R.color.default_underline_indicator_selected_color)

        // Retrieve styles attributes
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.UnderlinePageIndicator, defStyle, 0)
        fades = a.getBoolean(R.styleable.UnderlinePageIndicator_fades, defaultFades)
        selectedColor =
            a.getInteger(R.styleable.UnderlinePageIndicator_selectedPageColor, defaultSelectedColor)
        fadeDelay = a.getInteger(R.styleable.UnderlinePageIndicator_fadeDelay, defaultFadeDelay)
        fadeLength = a.getInteger(R.styleable.UnderlinePageIndicator_fadeLength, defaultFadeLength)
        val background = a.getDrawable(R.styleable.UnderlinePageIndicator_android_background)
        if (background != null) {
            this.background = background
        }
        a.recycle()
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledPagingTouchSlop
    }
}