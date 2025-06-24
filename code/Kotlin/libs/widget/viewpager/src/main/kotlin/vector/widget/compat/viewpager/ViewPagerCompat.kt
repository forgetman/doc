package vector.widget.compat.viewpager

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

typealias OnScrollStateChanged = (state: ScrollState) -> Unit

enum class ScrollState {
    IDLE,
    DRAGGING,
    SETTLING
}

typealias OnScrolled = (
    currPosition: Int,
    nextPosition: Int,
    positionOffset: Float,
    positionOffsetPixels: Int
) -> Unit

typealias OnSelected = (position: Int) -> Unit

typealias OnIntent = (position: Int) -> Unit

typealias OnDirection = (
    page: ViewPagerCompat.Direction.Page,
    slide: ViewPagerCompat.Direction.Slide
) -> Unit

class ViewPagerCompat {

    class Direction {
        /**
         * 页面滑动的方向
         */
        enum class Page {
            IDLE,
            LEFT,
            RIGHT
        }

        /**
         * 手指滑动的方向
         */
        enum class Slide {
            IDLE,
            LEFT,
            RIGHT
        }
    }

    class OnPageChangedListener internal constructor(
        private val onScrollStateChanged: OnScrollStateChanged? = null,
        private val onScrolled: OnScrolled? = null,
        private val onSelected: OnSelected? = null,
        private val onDirection: OnDirection? = null,
        private val onIntent: OnIntent? = null
    ) : ViewPager.OnPageChangeListener, ViewPager2.OnPageChangeCallback() {

        companion object {
            fun newBuilder(action: Builder.() -> Unit): Builder {
                val builder = Builder()
                action(builder)
                return builder
            }
        }

        private var lastPositionOffset: Float = 0f
        private var currPosition: Int = -1
        private var scrollingPosition: Int = -1
        private var lastScrollState: Int = ViewPager.SCROLL_STATE_IDLE

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                currPosition = scrollingPosition
                onSelected?.invoke(currPosition)
            }
            onScrollStateChanged?.invoke(ScrollState.entries[state])
            lastScrollState = state
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (currPosition == -1) {
                // 只接收一次作为[onSelected]的初始化
                currPosition = position
                scrollingPosition = position
                onSelected?.invoke(currPosition)
                return
            }

            if (lastScrollState == ViewPager.SCROLL_STATE_IDLE) return

            /**
             * 往后滑动的时候[position]不变, 往左滑动的时候[position]为当前position - 1
             */
            val nextPos = if (currPosition == position && positionOffset > 0f) {
                currPosition + 1
            } else position

            val slideDirection = when {
                lastPositionOffset > positionOffset -> Direction.Slide.RIGHT
                lastPositionOffset < positionOffset -> Direction.Slide.LEFT
                else -> Direction.Slide.IDLE
            }

            val pageDirection = when {
                nextPos > currPosition -> Direction.Page.RIGHT
                nextPos < currPosition -> Direction.Page.LEFT
                else -> Direction.Page.IDLE
            }
            onDirection?.invoke(pageDirection, slideDirection)

            val realOffset = when (pageDirection) {
                Direction.Page.LEFT -> 1f - positionOffset
                Direction.Page.RIGHT -> {
                    /**
                     * 最后一次回调的[positionOffset]为0, 与实际情况不符, 处理一下
                     */
                    if (positionOffset == 0f) 1f else positionOffset
                }

                else -> positionOffset
            }
            onScrolled?.invoke(currPosition, nextPos, realOffset, positionOffsetPixels)

            lastPositionOffset = positionOffset
            scrollingPosition = nextPos
        }

        override fun onPageSelected(position: Int) {
            onIntent?.invoke(position)
            if (lastScrollState == ViewPager.SCROLL_STATE_IDLE) {
                /**
                 * 如果没有滚动的情况下被调用了onPageSelected, 一般情况是更新了数据源
                 * 需要同步更新下标
                 */
                currPosition = position
                scrollingPosition = position
                onSelected?.invoke(currPosition)
            }
        }

        class Builder internal constructor() {
            var onScrollStateChanged: OnScrollStateChanged? = null
            var onScrolled: OnScrolled? = null
            var onSelected: OnSelected? = null
            var onDirection: OnDirection? = null
            var onIntent: OnIntent? = null

            fun build() =
                OnPageChangedListener(
                    onScrollStateChanged,
                    onScrolled,
                    onSelected,
                    onDirection,
                    onIntent
                )
        }
    }
}
