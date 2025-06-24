package vector.widget.swiperefresh.header

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.graphics.withClip
import androidx.core.view.doOnLayout
import vector.app.ext.inflate

/**
 * @author yuansui
 */
abstract class BaseLayoutSwipeHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseSwipeHeader(context, attrs, defStyleAttr) {

    protected var offset: Int = 0
    private var contentHeight: Int = 0

    @get:LayoutRes
    abstract val layoutId: Int

    init {
        @Suppress("LeakingThis")
        flowOfSetup()

        doOnLayout {
            /**
             * 不能在[View.onMeasure]里赋值
             * [vector.widget.swiperefresh.SwipeRefreshLayout]会经常调用此方法做更改
             */
            contentHeight = contentView.height
            offset = -contentHeight
        }

    }

    override fun createContentView(): View {
        return context.inflate(layoutId)
    }

    protected open fun flowOfSetup() {
    }

    override fun offset(offset: Int) {
        this.offset += offset
        postInvalidateOnAnimation()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (width == 0) {
            return
        }

        canvas.withClip(0, 0, width, contentHeight) {
            /**
             * 方法1: 先位移后clip(不好理解)
             */
            //        canvas.translate(0, mOffset);
            //        canvas.clipRect(0, getPaddingTop() + mContentHeight, mWidth, getPaddingTop() + Math.abs(mOffset));
            /**
             * 方法2: 先clip后位移(好理解)
             */
            //        canvas.clipRect(0, getPaddingTop(), mWidth, mContentHeight + getPaddingTop());
            canvas.translate(0f, offset.toFloat())

            super.dispatchDraw(canvas)
        }
    }

}
