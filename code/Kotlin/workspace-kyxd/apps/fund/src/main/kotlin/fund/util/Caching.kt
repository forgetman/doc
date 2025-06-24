package fund.util

import vector.util.CachingEx

/**
 * @author yuansui
 * @since 2018/7/23
 */
object Caching : CachingEx() {

    const val BMP_CACHE_DISK_NAME = "/bmp/"
    private const val CAMERA_CACHE_DISK_NAME = "/camera/"

    var cameraCacheDir: String? = null
        private set

    init {
        makeDir(BMP_CACHE_DISK_NAME)
        cameraCacheDir = makeDir(CAMERA_CACHE_DISK_NAME)
    }
}