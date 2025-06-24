package vector.widget.swiperefresh.footer

import android.content.Context
import android.view.View
import vector.app.ext.bind.bindView
import vector.app.ext.view.gone
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.ext.view.show
import vector.widget.ProgressView
import vector.widget.swiperefresh.R
import vector.widget.swiperefresh.delegate.LoadMore

/**
 * @author yuansui
 */
class DefaultFooter(context: Context) : BaseFooter(context) {

    private val layoutLoading by bindView<View>(R.id.scrollable_footer_layout_loading)
    private val layoutReloadMore by bindView<View>(R.id.scrollable_footer_tv_reload_more)
    private val progressView by bindView<ProgressView>(R.id.scrollable_footer_progress_view)

    override val contentViewId: Int
        get() = R.layout.layout_scrollable_footer

    init {
        layoutReloadMore.setOnDebounceClickListener {
            onRetryClick?.invoke(it)
        }
    }

    override fun onStateChanged(state: LoadMore.State) {
        when (state) {
            LoadMore.State.READY -> onStart()
            LoadMore.State.LOADING -> onLoading()
            LoadMore.State.DETACH -> onStop()
            LoadMore.State.ERROR -> onError()
        }
    }

    private fun onStart() {
        layoutLoading.show()
        layoutReloadMore.gone()
    }

    private fun onLoading() {
        layoutLoading.show()
        layoutReloadMore.gone()
        progressView.start()
    }

    private fun onStop() {
        layoutLoading.gone()
        layoutReloadMore.gone()
        progressView.stop()
    }

    private fun onError() {
        layoutLoading.gone()
        layoutReloadMore.show()
        progressView.stop()
    }
}
