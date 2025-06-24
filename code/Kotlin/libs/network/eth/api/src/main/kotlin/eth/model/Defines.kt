package eth.model

enum class ContentTypeValue(val desc: String) {
    DEFAULT("application/x-www-form-urlencoded"),
    FORM("multipart/form-data"),
    JSON("application/json"),
    JSON_FAKE("application/json"), // 应对要求json格式，但实际上是表单提交的情况(内容非json格式)
}

enum class CharsetValue(val desc: String) {
    GBK("GBK"),
    UTF8("UTF-8"),
    ISO_8859_1("ISO-8859-1"),
}

/**
 * http的请求方式
 */
enum class HttpMethod {
    GET, // get请求
    POST, // post请求
    UPLOAD, // 上传(post请求)
    DOWNLOAD, // 下载(get请求)
    PUT, // put请求
    DELETE, // delete请求
}

object ErrorDefaultCode {
    const val PARSE = "-9001" // json解析错误
    const val NETWORK = "-9002" // 无网
    const val CONNECT = "-9003" // 有网, 但连接失败
    const val CREATE_FILE_FAIL = "-9004"  // 创建文件失败
    const val DOWNLOAD_RETURN_TYPE_ERROR = "-9005"  // 下载返回类型不是DownloadResult
    const val EMPTY_CONVERTER = "-9006"  // 没有设置converter
    const val NON_HANDLED_CONVERTER = "-9007"  // 没有可以处理当前数据的converter
    const val DATA_NOT_EXIST = "-9008" // 无法解析出正确的data数据
}