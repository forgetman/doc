package vector.widget.swiperefresh.header

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import vector.app.config.Config
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.widget.swiperefresh.SwipeRefreshLayout
import vector.widget.swiperefresh.delegate.SwipeRefresh

/**
 * @author yuansui
 */
@Suppress("LeakingThis")
abstract class BaseSwipeHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    protected var percent = 0f
        private set

    protected var contentView: View

    protected var lastUiState = SwipeRefresh.UiState.IDLE
        private set

    init {
        contentView = createContentView()
        // base
        val dimension = if (isInEditMode) {
            SwipeRefreshLayout.DRAG_DISTANCE_MAX
        } else {
            Config.list().dragDistance ?: SwipeRefreshLayout.DRAG_DISTANCE_MAX
        }
        // offset
        val h = dimension.toPx(context) * 1.3f
        addView(contentView, LayoutParamsFactory.linear(MATCH_PARENT, h.toInt()))
    }

    protected abstract fun createContentView(): View

    fun setPercent(percent: Float, invalidate: Boolean) {
        this.percent = percent
        onPercentChanged(percent, invalidate)
    }

    open fun onPercentChanged(percent: Float, invalidate: Boolean) {}

    abstract fun offset(offset: Int)

    fun changeUiStyle(style: SwipeRefresh.UiState) {
        if (lastUiState == style) {
            return
        }

        onStyleChanged(style)
        lastUiState = style
    }

    abstract fun onStyleChanged(style: SwipeRefresh.UiState)
}
