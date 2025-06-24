package eth.internal

import eth.annotation.BooleanHeaders
import eth.annotation.Charset
import eth.annotation.ContentType
import eth.annotation.Customizes
import eth.annotation.StringHeaders
import eth.annotation.Host
import eth.annotation.IntHeaders
import eth.annotation.Retry
import eth.annotation.method.Delete
import eth.annotation.method.Download
import eth.annotation.method.Get
import eth.annotation.method.Post
import eth.annotation.method.Put
import eth.annotation.method.Upload
import eth.annotation.param.Body
import eth.annotation.param.Customize
import eth.annotation.param.File
import eth.annotation.param.Header
import eth.annotation.param.Path
import eth.annotation.param.Query
import eth.annotation.param.Url
import eth.model.ContentTypeValue
import eth.model.HttpMethod
import java.lang.reflect.Method

/**
 * @author : GuoXuan
 * @since : 2019/5/21
 */
internal object ServiceMethod {

    private const val SPLIT = "/"

    fun method(baseUrl: String?, method: Method, params: Array<Any?>?): eth.model.Request {
        return eth.model.Request.Builder()
            .parseMethodAnnotations(method.annotations, baseUrl)
            .parseParamAnnotations(method.parameterAnnotations, params)
            .type(method.genericReturnType)
            .build()
    }

    /**
     * 解析方法上的注解
     * <p>
     *     通过注解或者注解的value获取网络请求的url或headers
     * </p>
     * 目前支持的注解
     * <p>
     *  [Get]
     *  [Post]
     *  [Download]
     *  [Upload]
     *  [Delete]
     *  [Put]
     *  [StringHeaders]
     * </p>
     *
     * @param annotations 方法上的注解
     */
    private fun eth.model.Request.Builder.parseMethodAnnotations(
        annotations: Array<out Annotation?>?,
        baseUrl: String?
    ): eth.model.Request.Builder {
        if (annotations == null)
            throw IllegalArgumentException("Method must set Annotation")

        var host = baseUrl
        /**
         * 需要先找出[Host]类型的注解, 保证先确定host的值
         * 使用普通的for语法是为了host的smart cast能成立(如果用block语法, 编译器会认为block会改变host的值)
         */
        for (annotation in annotations) {
            if (annotation is Host) {
                host = annotation.value
                break
            }
        }

        if (host.isNullOrEmpty()) throw IllegalArgumentException("Host must set value")

        // parse the method Annotations about HttpMethod and Url or Header
        annotations.forEach { annotation ->
            when (annotation) {
                is Get -> {
                    method(HttpMethod.GET)
                    url(host.hostPlus(annotation.value))
                }

                is Post -> {
                    method(HttpMethod.POST)
                    url(host.hostPlus(annotation.value))
                }

                is Download -> {
                    method(HttpMethod.DOWNLOAD)
                    url(host.hostPlus(annotation.value))
                        .useCheckPoint(annotation.useCheckPoint)
                }

                is Upload -> {
                    method(HttpMethod.UPLOAD)
                    contentType(ContentTypeValue.FORM)
                    url(host.hostPlus(annotation.value))
                }

                is Put -> {
                    method(HttpMethod.PUT)
                    url(host.hostPlus(annotation.value))
                }

                is Delete -> {
                    method(HttpMethod.DELETE)
                    url(host.hostPlus(annotation.value))
                }

                is StringHeaders -> {
                    annotation.value.forEach {
                        header(it.key, it.value)
                    }
                }

                is IntHeaders -> {
                    annotation.value.forEach {
                        header(it.key, it.value)
                    }
                }

                is BooleanHeaders -> {
                    annotation.value.forEach {
                        header(it.key, it.value)
                    }
                }

                is Customizes -> {
                    annotation.value.forEach {
                        customize(it.key, it.value)
                    }
                }

                is Retry -> {
                    count(annotation.count)
                    delay(annotation.delay)
                }

                is ContentType -> {
                    contentType(annotation.value)
                }

                is Charset -> {
                    charset(annotation.value)
                }
            }
        }
        return this
    }

    private fun String.hostPlus(value: String): String {
        val hostEndWithSplit = this.endsWith(SPLIT)
        val valueStartWithSplit = value.startsWith(SPLIT)
        return when {
            hostEndWithSplit && valueStartWithSplit -> {
                // 两个都有, 去掉一个多余的
                this.plus(value.substring(1))
            }

            hostEndWithSplit || valueStartWithSplit -> {
                // 只有host或者value有
                this.plus(value)
            }

            else -> {
                if (value.isEmpty()) {
                    this.plus(value)
                } else {
                    // 两个都没有, 加上一个
                    this.plus(SPLIT).plus(value)
                }
            }
        }
    }

    /**
     * 解析参数上的注解
     * <p>
     *     通过注解或者注解的value获取网络请求的参数名
     * </p>
     * 目前支持的注解
     * <p>
     *  [Query]
     *  [File]
     *  [Url]
     *  [Header]
     *  [Path]
     * </p>
     * @param annotations 参数前的注解
     * @param params 参数
     */
    private fun eth.model.Request.Builder.parseParamAnnotations(
        annotations: Array<out Array<out Annotation?>?>?,
        params: Array<Any?>?
    ): eth.model.Request.Builder {
        if (annotations == null || params == null) return this

        val annotationsSize = annotations.size
        val paramSize = params.size

        // params number should more than annotations number
        require(paramSize >= annotationsSize) { "Argument count ($paramSize) doesn't match expected Annotation count ($annotationsSize" }

        var key: String
        var param: Any
        var paramAnnotations: Array<out Annotation?>

        for (i in 0 until paramSize) {
            param = params[i] ?: continue
            paramAnnotations = annotations[i] ?: continue

            fun checkNotEmpty(k: String, annotation: Annotation) {
                require(k.isNotEmpty()) { "${annotation::class.java.simpleName} Annotation must set value" }
            }

            // parse the params Annotations about param key and value
            paramAnnotations.forEach {
                when (it) {
                    is Query -> {
                        key = it.value
                        checkNotEmpty(key, it)
                        param(key, param)
                    }

                    is File -> {
                        key = it.value
                        checkNotEmpty(key, it)
                        paramFile(key, param)
                    }

                    is Url -> {
                        url(param.toString())
                    }

                    is Header -> {
                        key = it.value
                        checkNotEmpty(key, it)
                        header(key, param)
                    }

                    is Path -> {
                        key = it.value
                        checkNotEmpty(key, it)
                        replaceUrl("{$key}", param.toString())
                    }

                    is Customize -> {
                        key = it.value
                        customize(key, param)
                    }

                    is Body -> {
                        body(param)
                    }
                }
            }
        }
        return this
    }
}