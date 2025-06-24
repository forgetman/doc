package eth.okhttp.task

import eth.model.ContentTypeValue
import eth.model.HttpMethod
import eth.model.Request
import okhttp.ext.OkRequest
import okhttp.ext.OkRequestBuilder
import eth.okhttp.body.ProgressRequestBody
import logger.L
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.nio.charset.Charset

/**
 * upload请求的任务
 * @author : GuoXuan
 * @since : 2019/5/31
 */
internal class UploadTask<T>(
    httpClient: OkHttpClient, request: Request
) : OkHttpTask<T>(httpClient, request) {

    override val realRequest: OkRequest
        get() = OkRequestBuilder()
            .addHeaders(request.headers)
            .requestUrl(request.url)
            .body(request)
            .build()

    private fun OkRequestBuilder.body(request: Request): OkRequestBuilder {
        if (!request.contentType.desc.contains("multipart")) {
            throw Exception(
                "you can use other ${HttpMethod::class.java.simpleName} " +
                        "or set other ${ContentTypeValue::class.java.simpleName} about of multipart"
            )
        }

        // 构建MediaType
        val mediaType = request.contentType.desc.toMediaTypeOrNull() ?: MultipartBody.FORM
        val type = request.charset?.desc
        val charset = mediaType.charset()?.name()
        if (type != null && type != charset) {
            mediaType.charset(Charset.forName(type))
        }

        // 构建body
        val body = MultipartBody.Builder()
        request.params?.forEach { body.addFormDataPart(it.key, it.value.toString()) }
        request.fileParams?.forEach {
            val fileName = it.value.toString()
            try {
                val requestBody = File(fileName).asRequestBody(mediaType)
                body.addFormDataPart(it.key, fileName, requestBody)
            } catch (e: Exception) {
                L.e(e)
            }
        }
        body.setType(mediaType)
        val progressBody = ProgressRequestBody(body.build(), progressListener)
        post(progressBody)
        return this
    }

}