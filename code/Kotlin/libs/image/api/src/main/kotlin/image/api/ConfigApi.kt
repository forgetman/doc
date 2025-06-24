package image.api

import android.content.Context
import androidx.annotation.DrawableRes

/**
 * @author yuansui
 * @since 2020/11/19
 */
interface ConfigApi {
    fun setup(
        context: Context,
        cacheDir: String?,
        cacheSize: Long,
        crossFadeDuration: Int?,
        @DrawableRes error: Int?
    )
}