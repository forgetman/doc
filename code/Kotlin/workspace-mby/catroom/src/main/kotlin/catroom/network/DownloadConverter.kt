package catroom.network

import eth.convertor.AbstractDownloadConverter
import eth.model.Response
import vector.util.Dir

/**
 * @author yuansui
 * @since 2024/7/12
 */
class DownloadConverter : AbstractDownloadConverter() {

    private val Dir.downloadCacheDir: String
        get() = mkCacheDir("download")

    override fun dirName(response: Response): String {
        return Dir.downloadCacheDir
    }
}