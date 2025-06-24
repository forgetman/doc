@file:Suppress("unused")

package image.api.impl

import android.content.Context
import image.api.ConfigApi

/**
 * @author yuansui
 * @since 2020/11/19
 */
class ConfigApiImpl : ConfigApi {

    companion object {
        private const val DEFAULT_DURATION = 250

        var diskCacheDir: String? = null
        var diskCacheSize: Long = 1024 * 1024 * 200L
        var defaultCrossFadeDuration: Int = DEFAULT_DURATION
        var defaultError: Int? = null
    }

    override fun setup(
        context: Context,
        cacheDir: String?,
        cacheSize: Long,
        crossFadeDuration: Int?,
        error: Int?
    ) {
        diskCacheDir = cacheDir
        diskCacheSize = cacheSize
        defaultError = error
        defaultCrossFadeDuration = crossFadeDuration ?: return
    }

}