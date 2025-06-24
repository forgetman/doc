@file:Suppress("unused")

package image.api.impl

import android.widget.ImageView
import coil3.dispose
import image.api.ClearApi

/**
 * @author yuansui
 * @since 2021/8/16
 */
class ClearApiImpl : ClearApi {

    override fun clear(imageView: ImageView) {
        imageView.dispose()
    }
}