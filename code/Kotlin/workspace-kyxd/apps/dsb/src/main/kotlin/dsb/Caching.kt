package dsb

import vector.util.CachingEx

/**
 * @author yuansui
 * @since 2017/6/26
 */

object Caching : CachingEx() {
    private const val BMP_CACHE_DIR = "bmp"

    val imageCacheDir: String? by lazy {
        mkCacheDir(BMP_CACHE_DIR)
    }
}
