package vector.widget.scrollable.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.withSave
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *([RecyclerView.LayoutManager]是[GridLayoutManager])
 * [RecyclerView]的[RecyclerView.ItemDecoration]
 * 分割线
 *
 * @author : GuoXuan
 * @since : 2019/02/07
 */
internal class GridDecoration(parent: RecyclerView, private val attrs: GridAttrs) :
    BaseDecoration(attrs) {

    private val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
    private val spanCount: Int = layoutManager.spanCount

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)
        if (RecyclerView.NO_POSITION == position) return

        val column = position % spanCount // 第几列

        outRect.left = column * size / spanCount
        outRect.right = size - (column + 1) * size / spanCount
        if (attrs.drawTop && position < spanCount) {
            outRect.top = size
        }
        if (attrs.drawBottom || position / spanCount != itemCount.minus(1) / spanCount) {
            outRect.bottom = size
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.withSave {

            var top: Int
            var bottom: Int
            var left: Int
            var right: Int

            var child: View
            var position: Int
            var column: Int // 处于第几列

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                child = parent.getChildAt(i) ?: continue

                position = parent.getChildLayoutPosition(child)
                if (RecyclerView.NO_POSITION == position) continue

                layoutManager.getDecoratedBoundsWithMargins(child, bounds)

                column = position % spanCount

                top = bounds.top + attrs.marginTop
                bottom = bounds.bottom - attrs.marginBottom
                if (attrs.drawTop && position < spanCount) {
                    // 第一行
                    top += size
                }
                if (attrs.drawBottom || position / spanCount != itemCount.minus(1) / spanCount) {
                    // 最后一行
                    bottom -= size
                }

                // 左边分割线
                if (column != 0) {
                    left = bounds.left
                    right = left + column * size / spanCount

                    drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                }

                // 右边分割线
                if (position.plus(1) % spanCount != 0) {
                    right = bounds.right
                    left = right - size + (column + 1) * size / spanCount

                    if (position != itemCount - 1) {
                        // 最后一个
                        drawRect(
                            left.toFloat(),
                            top.toFloat(),
                            right.toFloat(),
                            bottom.toFloat(),
                            paint
                        )
                    }
                }
            }

        }
    }

}