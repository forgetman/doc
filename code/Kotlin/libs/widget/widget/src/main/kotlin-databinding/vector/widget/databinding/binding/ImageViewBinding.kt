@file:Suppress("unused")

package vector.widget.databinding.binding

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.ImageView

/**
 * @author yuansui
 * @since 2018/1/26
 */
object ImageViewBinding {

    private const val FOREGROUND = BINDING_PREFIX + "imageView_foreground"
    private const val FOREGROUND_ID = BINDING_PREFIX + "imageView_foregroundId"

    @JvmStatic
    @BindingAdapter(FOREGROUND)
    fun setForeground(view: ImageView, foreground: Drawable) {
        view.foreground = foreground
    }

    @JvmStatic
    @BindingAdapter(FOREGROUND_ID)
    fun setForeground(view: ImageView, @DrawableRes id: Int) {
        view.setForeground(id)
    }
}