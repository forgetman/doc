package image

import android.content.Context
import androidx.annotation.DrawableRes
import image.api.ConfigApi

/**
 * @author yuansui
 * @since 2020/11/19
 */
class ConfigSetter : ConfigApi {

    companion object {
        private const val CLASS_NAME = "image.api.impl.ConfigApiImpl"
    }

    private val api: ConfigApi by lazy {
        val name = Class.forName(CLASS_NAME)
        name.getDeclaredConstructor().newInstance() as ConfigApi
    }

    override fun setup(
        context: Context,
        cacheDir: String?,
        cacheSize: Long,
        crossFadeDuration: Int?,
        @DrawableRes error: Int?
    ) {
        api.setup(context, cacheDir, cacheSize, crossFadeDuration, error)
    }

}