package image

import android.content.Context
import android.graphics.Bitmap
import image.api.ImageDownloadApi
import java.io.File

/**
 * @author yuansui
 * @since 2019-05-15
 */
class ImageDownloader : ImageDownloadApi {

    companion object {
        private const val CLASS_NAME = "image.api.impl.ImageDownloadApiImpl"
    }

    private val api: ImageDownloadApi by lazy {
        val name = Class.forName(CLASS_NAME)
        name.getDeclaredConstructor().newInstance() as ImageDownloadApi
    }

    override fun toBitmap(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (Bitmap?) -> Unit
    ) {
        api.toBitmap(context, url, width, height, result)
    }

    override fun toFile(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (File?) -> Unit
    ) {
        api.toFile(context, url, width, height, result)
    }

}