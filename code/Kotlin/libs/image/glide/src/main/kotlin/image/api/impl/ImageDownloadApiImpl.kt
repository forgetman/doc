@file:Suppress("unused")

package image.api.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import image.api.ImageDownloadApi
import image.glide.GlideLoader
import java.io.File

/**
 * @author yuansui
 * @since 2020/11/19
 */
class ImageDownloadApiImpl : ImageDownloadApi {

    override fun toBitmap(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (Bitmap?) -> Unit
    ) {
        GlideLoader.with(context)
            .asBitmap()
            .load(url)
            .override(width ?: -1, height ?: -1)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    result(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    result(null)
                }
            })
    }

    override fun toFile(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (File?) -> Unit
    ) {
        GlideLoader.with(context)
            .asFile()
            .load(url)
            .override(width ?: -1, height ?: -1)
            .into(object : CustomTarget<File>() {
                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    result(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    result(null)
                }
            })
    }
}