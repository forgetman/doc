package eth.okhttp.task

import eson.Eson
import eth.model.ContentTypeValue
import eth.model.Request
import eth.okhttp.body.ProgressRequestBody
import okhttp.ext.OkRequest
import okhttp.ext.OkRequestBuilder
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.nio.charset.Charset

/**
 * post请求的任务
 */
internal open class PostTask<T>(httpClient: OkHttpClient, request: Request) :
    OkHttpTask<T>(httpClient, request) {

    override val realRequest: OkRequest
        get() = OkRequestBuilder()
            .addHeaders(request.headers)
            .requestUrl(request.url)
            .body(request)
            .build()

    private fun OkRequestBuilder.body(request: Request): OkRequestBuilder {
        val body = when (request.contentType) {
            ContentTypeValue.DEFAULT -> {
                val body = request.body
                if (body == null) {
                    FormBody.Builder()
                        .add(request.params)
                        .build()
                } else {
                    val mediaType = ContentTypeValue.DEFAULT.desc.toMediaTypeOrNull()
                    val type = request.charset?.desc
                    val charset = mediaType?.charset()?.name()
                    if (type != null && type != charset) {
                        mediaType?.charset(Charset.forName(type))
                    }
                    body.toString().toRequestBody(mediaType)
                }
            }

            ContentTypeValue.FORM -> {
                FormBody.Builder()
                    .add(request.params)
                    .build()
            }

            ContentTypeValue.JSON -> {
                val toBeJson: Any? = request.body ?: request.params
                val content = Eson.create(escapeHtmlChars = false).toJson(toBeJson)
                val mediaType = ContentTypeValue.JSON.desc.toMediaTypeOrNull()
                val type = request.charset?.desc
                val charset = mediaType?.charset()?.name()
                if (type != null && type != charset) {
                    mediaType?.charset(Charset.forName(type))
                }
                content.toRequestBody(mediaType)
            }

            ContentTypeValue.JSON_FAKE -> {
                val mediaType = ContentTypeValue.JSON.desc.toMediaTypeOrNull()
                val type = request.charset?.desc
                val charset = mediaType?.charset()?.name()
                if (type != null && type != charset) {
                    mediaType?.charset(Charset.forName(type))
                }
                request.body.toString().toRequestBody(mediaType)
            }
        }

        val progressBody = ProgressRequestBody(body, progressListener)
        method(progressBody)
        return this
    }

    protected open fun OkRequestBuilder.method(progressBody: ProgressRequestBody) {
        post(progressBody)
    }

    private fun FormBody.Builder.add(params: Map<String, Any>?): FormBody.Builder {
        params?.forEach { add(it.key, it.value.toString()) }
        return this
    }

}