package eth.model

import eth.def.HeaderKey
import sugar.ext.self
import java.lang.reflect.Type

class Request internal constructor() {

    // 默认为get方式
    var method = HttpMethod.GET
        private set

    var contentType = ContentTypeValue.DEFAULT
        private set

    var charset: CharsetValue? = null
        private set

    // 断点续传
    var useCheckPoint: Boolean = false
        private set

    var host: String? = null
        private set

    var url: String? = null
        private set

    var returnType: Type? = null
        private set

    var headers: Map<String, Any>? = null
        private set

    var params: Map<String, Any>? = null
        private set

    var body: Any? = null
        private set

    var fileParams: Map<String, Any>? = null
        private set

    var customizeParams: Map<String, Any>? = null
        private set

    fun newBuilder(): Builder {
        return Builder(this)
    }

    /**
     * 内部builder
     */
    class Builder {

        private val headers by lazy { HashMap<String, Any>() } // 存放请求头的信息
        private val params by lazy { HashMap<String, Any>() } // 存放请求参数(不包括文件参数)
        private val fileParams by lazy { HashMap<String, Any>() } // 存放请求(文件)参数
        private val customizeParams by lazy { HashMap<String, Any>() } // 存放自定义参数(多作于识别)
        private var body: Any? = null

        private var method = HttpMethod.GET
        private var contentType: ContentTypeValue = ContentTypeValue.DEFAULT
        private var charset: CharsetValue? = null
        private var host: String? = null
        private var url: String? = null
        private var returnType: Type? = null
        private var useCheckPoint: Boolean = false

        constructor()

        internal constructor(request: Request) : this() {
            method = request.method
            url = request.url
            host = request.host
            contentType = request.contentType
            charset = request.charset
            body = request.body

            request.headers?.let { headers.putAll(it) }
            request.params?.let { params.putAll(it) }
            request.fileParams?.let { fileParams.putAll(it) }
            request.customizeParams?.let { customizeParams.putAll(it) }

            returnType = request.returnType
        }

        /**
         * 设置请求方式
         */
        fun method(method: HttpMethod) = self {
            this.method = method
        }

        /**
         * 设置url
         */
        fun url(url: String?) = self {
            this.url = url
        }

        /**
         * 替换url
         */
        fun replaceUrl(key: String, url: String) = self {
            this.url = this.url?.replace(key, url)
        }

        /**
         * 添加header数据
         */
        fun header(name: String, value: Any?) = self {
            value?.let { headers[name] = it }
        }

        /**
         * 添加body参数(不包括文件参数)
         */
        fun param(name: String, value: Any?) = self {
            value?.let { params[name] = it }
        }

        fun body(body: Any?) = self {
            this.body = body
        }

        /**
         * 添加body(文件)参数
         */
        fun paramFile(name: String, value: Any?) = self {
            value?.let { fileParams[name] = it }
        }

        /**
         * 添加自定义参数
         */
        fun customize(name: String, value: Any?) = self {
            value?.let { customizeParams[name] = it }
        }

        /**
         * 设置返回值类型
         */
        fun type(returnType: Type) = self {
            this.returnType = returnType
        }

        /**
         * 指定ContentType
         */
        fun contentType(type: ContentTypeValue) = self {
            contentType = type
        }

        /**
         * 指定charset
         */
        fun charset(charset: CharsetValue) = self {
            this.charset = charset
        }

        fun count(count: Int) = self {
            this.header(HeaderKey.RETRY_COUNT, count)
        }

        fun delay(delay: Long) = self {
            this.header(HeaderKey.RETRY_DELAY, delay)
        }

        fun useCheckPoint(use: Boolean) = self {
            this.useCheckPoint = use
        }

        fun build(): Request {
            return Request().let { r ->
                r.method = method
                r.url = url
                r.host = host

                r.contentType = contentType
                r.charset = charset

                r.useCheckPoint = useCheckPoint

                r.headers = headers
                r.params = params
                r.fileParams = fileParams
                r.customizeParams = customizeParams
                r.body = body

                r.returnType = returnType

                r
            }
        }

    }

}