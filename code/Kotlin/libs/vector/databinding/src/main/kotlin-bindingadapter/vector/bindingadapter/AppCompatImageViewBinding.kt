package vector.bindingadapter

import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter

/**
 * @author yuansui
 * @since 2023/1/12
 */
object AppCompatImageViewBinding {

    private const val DRAWABLE_ID = BINDING_PREFIX + "imageView_imageResource"

    @JvmStatic
    @BindingAdapter(DRAWABLE_ID)
    fun setImageResource(view: AppCompatImageView, @DrawableRes id: Int) {
        view.setImageResource(id)
    }
}