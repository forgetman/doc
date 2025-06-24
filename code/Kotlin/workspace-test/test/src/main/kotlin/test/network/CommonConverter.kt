package test.network

import eth.convertor.AbstractConverter
import eth.convertor.ConvertResult
import logger.L

private object Tag {
    const val CODE = "status"
    const val INFO = "info"
    const val DATA = "data"
}

private object ErrorCode {
    const val OK = "success"
}

class CommonConverter : AbstractConverter() {

//    override fun dirName(): String {
//        return Dir.downloadCacheDir
//    }

//    override fun downloadClass(): Array<Class<*>> {
//        return arrayOf(Api.DownloadResult::class.java)
//    }

//    @Suppress("UNCHECKED_CAST")
//    override fun <T> convertDownload(path: String, name: String, contentLength: Long): T? {
//        return Api.DownloadResult(contentLength, path, name) as? T
//    }

    override fun convert(code: Int, content: String?, request: eth.model.Request): ConvertResult {
        L.d(content)

        L.d("code = $code")
        val result = ConvertResult()
        result.success = true
        result.data = content

        return result
    }
}