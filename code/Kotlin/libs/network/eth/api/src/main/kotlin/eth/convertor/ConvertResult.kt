package eth.convertor

import java.io.File

/**
 * 解析结果
 * @author : GuoXuan
 * @modify: yuansui
 * @since : 2019/7/5
 */
open class ConvertResult {
    var success: Boolean = false // 服务器的成功失败
    var code: String? = null // 服务器的code
    var data: String? = null // 数据
    var msg: String? = null // 错误信息
    var error: Throwable? = null
}

internal class DownloadConvertResult : ConvertResult() {
    var contentLength: Long = 0L
    var path: String? = null
    var name: String? = null
}

data class DownloadResult(
    val contentLength: Long,
    val filePath: String,  // 文件路径
    val fileName: String  // 文件名
) {
    fun toFile() = File(filePath, fileName)

    override fun toString(): String {
        return "DownloadResult(length = $contentLength, path = $filePath, name = $fileName)"
    }
}