package vector.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * 因为本身ScrollView不可以外部监听, 所以封装一个可以给外部监听的scrollView
 *
 * @author yuansui
 */
open class ObservableScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var listener: OnScrollViewListener? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        listener?.onScrollChanged(l, t, oldl, oldt)
    }

    fun setOnScrollListener(l: OnScrollViewListener) {
        listener = l
    }

    interface OnScrollViewListener {
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }
}
