package eth.model

import java.io.InputStream

class Response(val request: Request, val code: Int = -1) {
    var body: ResponseBody? = null
}

class ResponseBody {
    var byteStream: InputStream? = null
    var contentLength: Long? = null
    var content: String? = null
}