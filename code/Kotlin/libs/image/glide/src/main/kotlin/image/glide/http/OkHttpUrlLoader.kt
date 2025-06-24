package image.glide.http

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

/**
 * @author yuansui
 * @since 2018/10/9
 */
class OkHttpUrlLoader(client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {

    private var client: Call.Factory? = client

    override fun buildLoadData(
        model: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(model, OkHttpStreamFetcher(client, model))
    }

    override fun handles(model: GlideUrl): Boolean {
        return true
    }

    class Factory constructor(private val client: Call.Factory) :
        ModelLoaderFactory<GlideUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {}
    }
}