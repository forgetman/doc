package vector.app.config

import androidx.annotation.DrawableRes
import image.ConfigSetter
import vector.appContext

class ImageConfig private constructor() {

    companion object {

        fun build(init: ImageConfig.() -> Unit): ImageConfig = ImageConfig().apply {
            init(this)

            val setter = ConfigSetter()
            setter.setup(appContext, cacheDir, cacheSize, crossFadeDuration, defaultError)
        }
    }

    var cacheDir: String? = null
    var cacheSize: Long = 1024 * 1024 * 200L  // 200MB
    var crossFadeDuration: Int? = null

    @DrawableRes
    var defaultError: Int? = null
}