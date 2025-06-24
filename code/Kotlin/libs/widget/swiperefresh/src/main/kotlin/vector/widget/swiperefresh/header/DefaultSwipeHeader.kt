package vector.widget.swiperefresh.header

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import vector.app.ext.bind.bindView
import vector.app.ext.view.gone
import vector.app.ext.view.show
import vector.widget.ProgressView
import vector.widget.swiperefresh.R
import vector.widget.swiperefresh.delegate.SwipeRefresh

/**
 * @author yuansui
 */
open class DefaultSwipeHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseLayoutSwipeHeader(context, attrs, defStyleAttr) {

    override val layoutId: Int
        get() = R.layout.layout_scrollable_header

    private val layoutRefresh by bindView<View>(R.id.scrollable_header_layout_refresh)
    private val ivArrow by bindView<View>(R.id.scrollable_header_iv_arrow)
    private val tvHint by bindView<TextView>(R.id.scrollable_header_tv_hint)
    private val layoutLoading by bindView<View>(R.id.scrollable_header_layout_loading)
    private val progressView by bindView<ProgressView>(R.id.scrollable_header_progress_view)

    private val animRotateUp: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_up_opposite)
    }

    private val animRotateDown: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_down_positive)
    }

    override fun onStyleChanged(style: SwipeRefresh.UiState) {
        when (style) {
            SwipeRefresh.UiState.IDLE -> onNormal()
            SwipeRefresh.UiState.READY -> onReady()
            SwipeRefresh.UiState.LOADING -> onLoading()
            SwipeRefresh.UiState.FINISH -> onFinish()
        }
    }

    private fun onNormal() {
        layoutRefresh.show()
        layoutLoading.gone()
        progressView.stop()

        if (lastUiState === SwipeRefresh.UiState.READY) {
            ivArrow.startAnimation(animRotateDown)
        } else if (lastUiState === SwipeRefresh.UiState.LOADING) {
            ivArrow.clearAnimation()
        }

        tvHint.setText(R.string.swipe_refresh_hint_normal)
    }

    private fun onReady() {
        layoutRefresh.show()
        layoutLoading.gone()

        if (lastUiState !== SwipeRefresh.UiState.READY) {
            ivArrow.clearAnimation()
            ivArrow.startAnimation(animRotateUp)
            tvHint.setText(R.string.swipe_refresh_hint_ready)
        }
    }

    private fun onLoading() {
        ivArrow.clearAnimation()
        layoutRefresh.gone()
        layoutLoading.show()
        progressView.start()
    }

    private fun onFinish() {
        layoutRefresh.gone()
        layoutLoading.show()
    }
}
