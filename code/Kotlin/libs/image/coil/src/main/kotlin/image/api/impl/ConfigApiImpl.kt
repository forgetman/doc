@file:Suppress("unused")

package image.api.impl

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.allowRgb565
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import image.api.ConfigApi
import okhttp3.OkHttpClient
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

/**
 * @author yuansui
 * @since 2020/11/19
 */
class ConfigApiImpl : ConfigApi {

    companion object {
        const val DEFAULT_DURATION = 250
    }

    override fun setup(
        context: Context,
        cacheDir: String?,
        cacheSize: Long,
        crossFadeDuration: Int?,
        errorDrawableId: Int?
    ) {
        val builder = ImageLoader.Builder(context)
            .allowRgb565(true)
            .crossfade(crossFadeDuration ?: DEFAULT_DURATION)
            .components {
                if (isSdkAtLeast(SdkInt.P_28)) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(SvgDecoder.Factory())

                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            OkHttpClient()
                        }
                    )
                )
            }
            .memoryCache {
                MemoryCache.Builder()
                    // Set the max size to 25% of the app's available memory.
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }

        if (errorDrawableId != null) {
            builder.error { request ->
                request.error() ?: context.getDrawable(errorDrawableId)?.asImage()
            }
        }

        SingletonImageLoader.setSafe { context ->
            builder.build()
        }
    }
}