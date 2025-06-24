package image.api

import android.widget.ImageView

/**
 * @author yuansui
 * @since 2021/8/13
 */
interface ClearApi {
    fun clear(imageView: ImageView)
}

private const val CLASS_NAME = "image.api.impl.ClearApiImpl"

fun ImageView.clear() {
    val name = Class.forName(CLASS_NAME)
    val inst = name.getDeclaredConstructor().newInstance() as ClearApi
    inst.clear(this)
}