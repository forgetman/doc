package image.api.impl

import android.widget.ImageView
import image.api.ClearApi
import image.glide.GlideLoader

/**
 * @author yuansui
 * @since 2021/8/13
 */
class ClearApiImpl : ClearApi {
    override fun clear(imageView: ImageView) {
        GlideLoader.with(imageView).clear(imageView)
    }
}