package vector.bindingadapter

import androidx.databinding.BindingAdapter
import vector.widget.ProgressView

/**
 * @author yuansui
 * @since 2018/12/28
 */
object ProgressViewBinding {

    private const val ANIMATION_STATUS = BINDING_PREFIX + "progressView_animation_toggle"

    @JvmStatic
    @BindingAdapter(ANIMATION_STATUS)
    fun setAnimStatus(view: ProgressView, bool: Boolean) {
        if (bool) view.start() else view.stop()
    }
}