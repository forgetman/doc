package vector.widget.scrollable.decoration

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author : GuoXuan
 * @since : 2019/2/28 0028
 */
internal abstract class BaseDecoration(attrs: BaseAttrs?) : RecyclerView.ItemDecoration() {

    protected val bounds by lazy { Rect() }

    protected val paint: Paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
    }

    protected var itemCount: Int = 0

    @ColorInt
    var color: Int = Color.GRAY
        private set(value) {
            field = value
            paint.color = value
        }

    var size: Int = 1
        private set

    var headerOffset: Int = 0
        private set
    var footerOffset: Int = 0
        private set

    init {
        attrs?.let {
            color = it.color
            size = it.size ?: 1

            headerOffset = it.headerOffset
            footerOffset = it.footerOffset
        }
    }

    @CallSuper
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.itemCount?.let {
            itemCount = it
        }
    }

}