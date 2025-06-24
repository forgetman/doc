/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.ViewPager
import vector.app.os.dp
import vector.widget.indicator.R
import vector.widget.viewpager.adapter.ItemPagerAdapter

/**
 * This androidx.recyclerview.widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
class TabPageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    HorizontalScrollView(context, attrs), PageIndicator {
    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        fun onTabReselected(position: Int)
    }

    private var mTabSelector: Runnable? = null
    private var selectTextColor = -0xbb5501
    private var selectTextColorNor = -0x99999a
    private var tabTextSize = 16f

    @DrawableRes
    private val mResId // 用法参考 IconPageIndicator
            = 0
    private val mTabClickListener = OnClickListener { view ->
        val tabView = view as TabView
        val oldSelected = mViewPager!!.currentItem
        val newSelected = tabView.index
        mViewPager!!.currentItem = newSelected
        if (oldSelected == newSelected && mTabReselectedListener != null) {
            mTabReselectedListener!!.onTabReselected(newSelected)
        }
    }
    private val mTabLayout: IcsLinearLayout
    private var mViewPager: ViewPager? = null
    private var mMaxTabWidth = 0
    private var mSelectedTabIndex = 0
    private var mTabReselectedListener: OnTabReselectedListener? = null
    fun setOnTabReselectedListener(listener: OnTabReselectedListener?) {
        mTabReselectedListener = listener
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val lockedExpanded = widthMode == MeasureSpec.EXACTLY
        isFillViewport = lockedExpanded
        val childCount = mTabLayout.childCount
        mMaxTabWidth =
            if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
                if (childCount > 2) {
                    (MeasureSpec.getSize(widthMeasureSpec) * 0.4f).toInt()
                } else {
                    MeasureSpec.getSize(widthMeasureSpec) / 2
                }
            } else {
                -1
            }
        val oldWidth = measuredWidth
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newWidth = measuredWidth
        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex)
        }
    }

    private fun animateToTab(position: Int) {
        val tabView = mTabLayout.getChildAt(position)
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector)
        }
        mTabSelector = Runnable {
            val scrollPos = tabView.left - (width - tabView.width) / 2
            smoothScrollTo(scrollPos, 0)
            mTabSelector = null
        }
        post(mTabSelector)
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector)
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector)
        }
    }

    private fun addTab(index: Int, text: CharSequence?, iconResId: Int) {
        val tabView = TabView(
            context
        )
        tabView.index = index
        tabView.isFocusable = true
        tabView.setOnClickListener(mTabClickListener)
        tabView.text = text
        tabView.gravity = Gravity.CENTER
        tabView.setTextColor(selectTextColorNor)
        tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.dp.toPx(this))
        if (iconResId != 0) {
            tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }
        mTabLayout.addView(
            tabView,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        )
    }

    override fun onPageScrollStateChanged(arg0: Int) {
    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
    }

    override fun onPageSelected(arg0: Int) {
        setCurrentItem(arg0)
    }

    override fun setViewPager(viewPager: ViewPager) {
        if (mViewPager === viewPager) {
            return
        }
        if (mViewPager != null) {
            mViewPager!!.removeOnPageChangeListener(this)
        }
        viewPager.adapter
            ?: throw IllegalStateException("ViewPager does not have adapter instance.")
        mViewPager = viewPager
        mViewPager!!.addOnPageChangeListener(this)
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        mTabLayout.removeAllViews()
        val adapter = mViewPager!!.adapter
        var iconAdapter: ItemPagerAdapter? = null
        if (adapter is ItemPagerAdapter) {
            iconAdapter = adapter
        }
        val count = adapter!!.count
        for (i in 0 until count) {
            var title = adapter.getPageTitle(i)
            if (title == null) {
                title = EMPTY_TITLE
            }
            var iconResId = 0
            if (iconAdapter != null) {
                iconResId = mResId
            }
            addTab(i, title, iconResId)
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1
        }
        setCurrentItem(mSelectedTabIndex)
        requestLayout()
    }

    override fun setViewPager(viewPager: ViewPager, initialPosition: Int) {
        setViewPager(viewPager)
        setCurrentItem(initialPosition)
    }

    override fun setCurrentItem(item: Int) {
        checkNotNull(mViewPager) { "ViewPager has not been bound." }
        mSelectedTabIndex = item
        mViewPager!!.currentItem = item
        val tabCount = mTabLayout.childCount
        for (i in 0 until tabCount) {
            val child = mTabLayout.getChildAt(i)
            val tabView = child as TabView
            tabView.setTextColor(selectTextColorNor)
            val isSelected = i == item
            child.setSelected(isSelected)
            if (isSelected) {
                animateToTab(item)
                tabView.setTextColor(selectTextColor)
            }
        }
    }

    private inner class TabView(context: Context) :
        AppCompatTextView(context, null, R.attr.vpiTabPageIndicatorStyle) {
        var index = 0
        public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth in 1 until measuredWidth) {
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                    heightMeasureSpec
                )
            }
        }
    }

    companion object {
        /**
         * Title text used when no title is provided by the adapter.
         */
        private val EMPTY_TITLE: CharSequence = ""
    }

    init {
        isHorizontalScrollBarEnabled = false
        mTabLayout = IcsLinearLayout(context, defStyleAttr = R.attr.vpiTabPageIndicatorStyle)
        addView(
            mTabLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        val a = context.obtainStyledAttributes(attrs, R.styleable.TabPageIndicator)
        selectTextColor = a.getInteger(R.styleable.TabPageIndicator_selectedTextColor, -0xbb5501)
        selectTextColorNor = a.getInteger(R.styleable.TabPageIndicator_normalTextColor, -0x99999a)
        tabTextSize = a.getInteger(R.styleable.TabPageIndicator_tabTextSize, 16).toFloat()
        a.recycle()
    }
}