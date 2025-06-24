package vector.widget.indicator.page

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import vector.util.LayoutParamsFactory.linear
import vector.widget.indicator.R
import vector.widget.viewpager.adapter.ItemPagerAdapter

/**
 * This androidx.recyclerview.widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
class IconPageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : HorizontalScrollView(context, attrs), PageIndicator {
    private val mIconsLayout: IcsLinearLayout
    private var viewPager: ViewPager? = null
    private var mIconSelector: Runnable? = null
    private var mSelectedIndex = 0
    private var mIndicatorSpace = 0
    private var mIndicatorSize = 0

    @DrawableRes
    private var mResId = 0
    private fun animateToIcon(position: Int) {
        val iconView: View = mIconsLayout.getChildAt(position)
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector)
        }
        mIconSelector = Runnable {
            val scrollPos = iconView.left - (width - iconView.width) / 2
            smoothScrollTo(scrollPos, 0)
            mIconSelector = null
        }
        post(mIconSelector)
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mIconSelector != null) {
            // Re-post the selector we saved
            post(mIconSelector)
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector)
        }
    }

    override fun onPageScrollStateChanged(arg0: Int) {
    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
    }

    override fun onPageSelected(position: Int) {
        var pos = position
        val iconAdapter = viewPager?.adapter as? ItemPagerAdapter? ?: return
        pos = if (iconAdapter.itemCycle) pos % iconAdapter.realCount else pos
        setCurrentItem(pos)
    }

    override fun setViewPager(viewPager: ViewPager) {
        if (this.viewPager === viewPager) {
            return
        }
        this.viewPager?.removeOnPageChangeListener(this)
        viewPager.adapter ?: throw IllegalStateException("ViewPager does not have adapter instance.")
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(this)
        notifyDataSetChanged()
    }

    fun setIndicatorSpace(px: Int) {
        mIndicatorSpace = px
    }

    fun setIndicatorSize(px: Int) {
        mIndicatorSize = px
    }

    fun setResId(@DrawableRes id: Int) {
        mResId = id
    }

    override fun notifyDataSetChanged() {
        mIconsLayout.removeAllViews()
        val iconAdapter = viewPager?.adapter as? ItemPagerAdapter? ?: return
        val count = if (iconAdapter.itemCycle) iconAdapter.realCount else iconAdapter.count
        for (i in 0 until count) {
            val view = ImageView(context, null, R.attr.vpiIconPageIndicatorStyle)
            view.setImageResource(mResId)
            if (mIndicatorSpace > 0) {
                view.setPadding(mIndicatorSpace, 0, mIndicatorSpace, 0)
            }
            if (mIndicatorSize > 0) {
                mIconsLayout.addView(view, linear(mIndicatorSize, mIndicatorSize))
            } else {
                mIconsLayout.addView(view)
            }
        }
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1
        }
        setCurrentItem(mSelectedIndex)
        requestLayout()
    }

    override fun setViewPager(viewPager: ViewPager, initialPosition: Int) {
        setViewPager(viewPager)
        setCurrentItem(initialPosition)
    }

    override fun setCurrentItem(item: Int) {
        checkNotNull(viewPager) { "ViewPager has not been bound." }
        mSelectedIndex = item
        if ((viewPager?.adapter as ItemPagerAdapter?)?.itemCycle == false) {
            viewPager?.currentItem = item
        }
        val tabCount: Int = mIconsLayout.childCount
        for (i in 0 until tabCount) {
            val child: View = mIconsLayout.getChildAt(i)
            val isSelected = i == item
            child.isSelected = isSelected
            // FIXME: 适应不同size的drawable, 期望有更好的方式
            child.requestLayout()
            if (isSelected) {
                animateToIcon(item)
            }
        }
    }

    class Config private constructor() {
        @Dimension(unit = Dimension.DP)
        var space = 0f
            private set

        @Dimension(unit = Dimension.DP)
        var size = 0
            private set

        @DrawableRes
        var resId = 0
            private set

        class Builder private constructor() {
            @Dimension(unit = Dimension.DP)
            private var mSpace = 0f

            @Dimension(unit = Dimension.DP)
            private var mSize = 0

            @DrawableRes
            private var mResId = 0
            fun space(@Dimension(unit = Dimension.DP) space: Float): Builder {
                mSpace = space
                return this
            }

            fun space(@Dimension(unit = Dimension.DP) space: Int): Builder {
                mSpace = space.toFloat()
                return this
            }

            fun size(@Dimension(unit = Dimension.DP) size: Int): Builder {
                mSize = size
                return this
            }

            fun resId(@DrawableRes resId: Int): Builder {
                mResId = resId
                return this
            }

            fun build(): Config {
                val config = Config()
                config.space = mSpace
                config.resId = mResId
                config.size = mSize
                return config
            }

            companion object {
                fun create(): Builder {
                    return Builder()
                }
            }
        }
    }

    init {
        isHorizontalScrollBarEnabled = false
        mIconsLayout = IcsLinearLayout(context, defStyleAttr = R.attr.vpiIconPageIndicatorStyle)
        addView(
            mIconsLayout,
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        )
    }
}