package vector.widget.scrollable.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * [RecyclerView]的[RecyclerView.ItemDecoration]
 * 分割线
 * PS:
 * [DecorationConfig]记录配置属性
 *
 * [DecorationWrapper.getItemOffsets]或[DecorationWrapper.onDraw]时才构建
 * [RecyclerView.ItemDecoration]
 *
 * @author : GuoXuan
 * @since : 2019/02/01
 */
internal class DecorationWrapper(private val attrs: BaseAttrs) : RecyclerView.ItemDecoration() {

    private var itemDecoration: RecyclerView.ItemDecoration? = null

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (itemDecoration == null) {
            initItemDecoration(parent)
        }
        itemDecoration?.getItemOffsets(outRect, view, parent, state)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (itemDecoration == null) {
            initItemDecoration(parent)
        }
        itemDecoration?.onDraw(c, parent, state)
    }

    private fun initItemDecoration(recyclerView: RecyclerView) {
        when (recyclerView.layoutManager) {
            is GridLayoutManager -> {
                itemDecoration = GridDecoration(
                    recyclerView, attrs as? GridAttrs
                        ?: throw IllegalStateException("The config is not Grid attrs")
                )
            }

            is LinearLayoutManager -> {
                itemDecoration = LinearDecoration(
                    recyclerView, attrs as? LinearAttrs
                        ?: throw IllegalStateException("THe config is not Linear attrs")
                )
            }
        }
    }

}
