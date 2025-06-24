package vector.widget.swiperefresh.footer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import vector.app.ext.inflate
import vector.app.ext.view.gone
import vector.app.ext.view.show
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.widget.swiperefresh.delegate.LoadMore

/**
 * @author yuansui
 */
@Suppress("LeakingThis")
abstract class BaseFooter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val contentView: View = context.inflate(contentViewId)

    var state = LoadMore.State.DETACH
        private set

    @get:LayoutRes
    protected abstract val contentViewId: Int

    var onRetryClick: ((View) -> Unit)? = null

    init {
        addView(contentView, LayoutParamsFactory.linear(MATCH_PARENT, WRAP_CONTENT))
        setViews()
    }

    open fun setViews() {}

    fun hide() {
        contentView.gone()
    }

    fun show() {
        contentView.show()
    }

    fun changeState(newState: LoadMore.State) {
        if (this.state == newState) {
            return
        }

        onStateChanged(newState)
        this.state = newState
    }

    abstract fun onStateChanged(state: LoadMore.State)
}
