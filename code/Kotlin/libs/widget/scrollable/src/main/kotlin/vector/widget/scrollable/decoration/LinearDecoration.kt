package vector.widget.scrollable.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.withSave
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * ([RecyclerView.LayoutManager]是[LinearLayoutManager])
 * [RecyclerView]的[RecyclerView.ItemDecoration]
 * 分割线
 *
 * @author : GuoXuan
 * @since : 2019/02/07
 * @modify: yuansui
 */
internal class LinearDecoration(parent: RecyclerView, private val attrs: LinearAttrs) :
    BaseDecoration(attrs) {

    private val orientation: Int = (parent.layoutManager as LinearLayoutManager).orientation

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        if (headerOffset != 0) {
            val head = position + 1
            if (head <= headerOffset) {
                if (head == headerOffset) {
                    // 偏移的最后一个item(下一个item的顶部(左)分割线)
                    if (orientation == RecyclerView.VERTICAL) {
                        if (attrs.drawTop) {
                            outRect.set(0, 0, 0, size)
                        }
                    } else {
                        if (attrs.drawStart) {
                            outRect.set(0, 0, size, 0)
                        }
                    }
                } else {
                    outRect.set(0, 0, 0, 0)
                }
                return // 后续不用再判断
            }
        }

        if (footerOffset != 0) {
            val foot = itemCount - position - 1
            if (foot <= footerOffset) {
                if (foot == footerOffset) {
                    // 偏移的第一个的前一个item(提前一个item的底部(右)分割线)
                    if (orientation == RecyclerView.VERTICAL) {
                        if (attrs.drawBottom) {
                            outRect.set(0, 0, 0, size)
                        }
                    } else {
                        if (attrs.drawEnd) {
                            outRect.set(0, 0, size, 0)
                        }
                    }
                } else {
                    outRect.set(0, 0, 0, 0)
                }
                return
            }
        }

        if (orientation == RecyclerView.VERTICAL) {
            // 上下偏移
            outRect.set(0, 0, 0, size)
            if (attrs.drawTop && position < 1) {
                // 需要画第一条顶部分割线
                outRect.top = size
            }

            if (!attrs.drawBottom && position == itemCount.minus(1)) {
                // 不需要画最后一条底部分割线
                outRect.bottom = 0
            }
        } else {
            // 左右偏移
            outRect.set(0, 0, size, 0)
            if (attrs.drawStart && position < 1) {
                // 需要画第一条左分割线
                outRect.left = size
            }

            if (!attrs.drawEnd && position == itemCount.minus(1)) {
                // 不需要画最后一条右分割线
                outRect.right = size
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == RecyclerView.VERTICAL) {
            drawHorizontal(c, parent)
        } else {
            drawVertical(c, parent)
        }
    }

    /**
     * 画垂直分割线[LinearLayoutManager.HORIZONTAL] ||
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        c.withSave {
            // 计算上下
            val top: Int
            val bottom: Int
            if (parent.clipToPadding) {
                top = parent.paddingTop + attrs.marginTop
                bottom = parent.height - parent.paddingBottom - attrs.marginBottom
                clipRect(
                    parent.paddingStart, top,
                    parent.width - parent.paddingEnd, bottom
                )
            } else {
                top = 0 + attrs.marginTop
                bottom = parent.height - attrs.marginBottom
            }
            // 计算左右
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i) ?: continue

                /**
                 * [RecyclerView.ItemDecoration.getItemOffsets]
                 * (Rect outRect, View view, RecyclerView parent, State state)
                 * (LayoutParams) view.getLayoutParams()).getViewLayoutPosition()
                 */
                val position = parent.getChildLayoutPosition(child)
                if (position == RecyclerView.NO_POSITION) continue

                parent.layoutManager?.getDecoratedBoundsWithMargins(child, bounds)

                if (headerOffset != 0) {
                    val head = position + 1
                    if (head < headerOffset) {
                        continue
                    } else if (head == headerOffset && !attrs.drawEnd) {
                        continue
                    }
                }

                if (attrs.drawStart && position < 1) {
                    // 需要画第一条左分割线
                    val left = bounds.left + child.translationX.roundToInt()
                    val right = left + size
                    drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                }

                if (!attrs.drawEnd && position == itemCount.minus(1)) {
                    // 不需要画最后一条右分割线
                    continue
                }
                val right = bounds.right + Math.round(child.translationX)
                val left = right - size
                drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }

    /**
     * 画水平分割线[LinearLayoutManager.VERTICAL] ==
     */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        c.withSave {
            // 计算左右
            val left: Int
            val right: Int
            if (parent.clipToPadding) {
                left = parent.paddingStart + attrs.marginStart
                right = parent.width - parent.paddingEnd - attrs.marginEnd
                clipRect(
                    left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom
                )
            } else {
                left = 0 + attrs.marginStart
                right = parent.width - attrs.marginEnd
            }
            // 计算上下
            val childCount = parent.childCount
            var top: Int
            var bottom: Int
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i) ?: continue

                val position = parent.getChildLayoutPosition(child)
                if (position == RecyclerView.NO_POSITION) continue

                if (footerOffset != 0) {
                    val foot = itemCount - position - 1
                    if (foot < footerOffset) {
                        continue
                    } else if (foot == footerOffset && !attrs.drawBottom) {
                        continue
                    }
                }

                parent.getDecoratedBoundsWithMargins(child, bounds)

                if (attrs.drawTop && position < 1) {
                    // 需要画第一条顶部分割线
                    top = bounds.top + child.translationY.roundToInt()
                    bottom = top + size
                    drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                }

                if (!attrs.drawBottom && position == itemCount.minus(1)) {
                    // 不需要画最后一条底部分割线
                    continue
                }
                bottom = bounds.bottom + child.translationY.roundToInt()
                top = bottom - size
                drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }

}