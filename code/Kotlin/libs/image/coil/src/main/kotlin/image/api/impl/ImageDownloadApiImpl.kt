package image.api.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import coil3.SingletonImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import image.api.ImageDownloadApi
import image.coil.ext.applyCanvas
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import logger.L
import sugar.ext.coroutineScope
import sugar.ext.doOnNotNull
import java.io.File

/**
 * @author yuansui
 * @since 2020-08-25
 */
class ImageDownloadApiImpl : ImageDownloadApi {

    @OptIn(DelicateCoroutinesApi::class)
    override fun toBitmap(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (Bitmap?) -> Unit
    ) {
        flow {
            val imageLoader = SingletonImageLoader.get(context)
            val builder = ImageRequest.Builder(context).data(url)

            doOnNotNull(width, height) { w, h ->
                builder.size(w, h)
            }

            val request = builder.build()
            val drawable = imageLoader.execute(request).image?.asDrawable(context.resources)
            emit(drawable)
        }.flowOn(Dispatchers.IO).filterNotNull().onEach {
            result(it.toBitmap())
        }.flowOn(Dispatchers.Main).catch { e ->
            L.e(e)
        }.launchIn(context.coroutineScope ?: GlobalScope)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun toFile(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        result: (File?) -> Unit
    ) {
        flow {
            val imageLoader = SingletonImageLoader.get(context)
            val builder = ImageRequest.Builder(context)
                .data(url)

            doOnNotNull(width, height) { w, h ->
                builder.size(w, h)
            }

            val request = builder.build()
            val drawable = imageLoader.execute(request).image?.asDrawable(context.resources)
            emit(drawable)
        }.map { drawable ->
            val bitmap = drawable?.toBitmap() ?: return@map null
            val f = File(context.cacheDir, "template")
            f.outputStream().buffered().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            f
        }.flowOn(Dispatchers.IO).onEach { file ->
            result(file)
            file?.deleteOnExit()
        }.flowOn(Dispatchers.Main).launchIn(context.coroutineScope ?: GlobalScope)
    }

    private fun Drawable.toBitmap(
        width: Int? = null,
        height: Int? = null,
        config: Bitmap.Config? = null
    ): Bitmap {
        if (this is BitmapDrawable) {
            val b = this.bitmap
            if (b != null) return b
        }
        val w: Int = width ?: this.intrinsicWidth.takeIf { it > 0 } ?: 512
        val h: Int = height ?: this.intrinsicHeight.takeIf { it > 0 } ?: 512

        return Bitmap.createBitmap(w, h, config ?: Bitmap.Config.ARGB_8888)
            .applyCanvas {
                val oldBounds = bounds
                setBounds(0, 0, w, h)
                draw(this)
                setBounds(oldBounds.left, oldBounds.top, oldBounds.right, oldBounds.bottom)
            }
    }
}