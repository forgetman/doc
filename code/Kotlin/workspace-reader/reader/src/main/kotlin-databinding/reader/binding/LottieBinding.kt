package reader.binding

import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import vector.app.ext.view.gone
import vector.app.ext.view.show
import vector.bindingadapter.BINDING_PREFIX

/**
 * @author yuansui
 * @since 2019-04-23
 */
object LottieBinding {

    private const val VISIBILITY = BINDING_PREFIX + "lottie_visibility"

    @JvmStatic
    @BindingAdapter(VISIBILITY)
    fun setVisibility(view: LottieAnimationView, visible: Boolean) {
        if (visible) {
            view.show()
            view.resumeAnimation()
        } else {
            view.gone()
            view.pauseAnimation()
        }
    }
}