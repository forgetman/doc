package test.network

import eth.convertor.AbstractDownloadConverter
import eth.model.Response
import test.downloadCacheDir
import vector.util.Dir

/**
 * @author yuansui
 * @since 2024/9/18
 */
class DownloadConverter : AbstractDownloadConverter() {
    override fun dirName(response: Response): String {
        return Dir.downloadCacheDir
    }
}