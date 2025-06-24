package vector.widget.style

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.annotation.StyleRes

internal abstract class StyleApplier<T : View>(protected val view: T) {

    protected val context: Context
        get() = view.context

    protected val resources: Resources
        get() = view.resources

    fun applyStyle(@StyleRes styleId: Int) {
        applyParent(styleId)
        onInitialize(styleId)
    }

    protected open fun applyParent(@StyleRes styleId: Int) {}

    protected abstract fun onInitialize(@StyleRes styleId: Int)
}