package image.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import image.api.impl.ConfigApiImpl
import image.glide.http.OkHttpUrlLoader
import image.glide.http.UnsafeOkHttpClient
import java.io.InputStream


/**
 * @author yuansui
 * @since 2018/2/8
 */
@GlideModule(glideName = "GlideLoader")
class ImageGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 路径规则: sdcard/Android/data/包名/cache/diskCacheName
        builder.setDiskCache(
            ExternalPreferredCacheDiskCacheFactory(
                context,
                "glide",
                ConfigApiImpl.diskCacheSize
            )
        )

        val calc = MemorySizeCalculator.Builder(context)
            .setBitmapPoolScreens(3f)
            .setMemoryCacheScreens(2f)
            .build()
        builder.setMemoryCache(LruResourceCache(calc.memoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(calc.bitmapPoolSize.toLong()))

        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(UnsafeOkHttpClient.getUnsafeOkHttpClient())
        )
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}