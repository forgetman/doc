package eth.okhttp.task

import eth.Task
import eth.def.AsyncResult
import eth.def.DownloadConst
import eth.def.Result
import eth.def.SyncResult
import eth.model.EthException
import eth.model.HttpMethod
import eth.model.Progress
import eth.model.Response
import eth.model.ResponseBody
import eth.okhttp.body.ProgressResponseBody
import okhttp.ext.OkRequest
import okhttp.ext.OkRequestBuilder
import okhttp.ext.OkResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import sugar.ext.safeClose
import sugar.ext.self
import sugar.ext.throwIfNull
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import kotlin.coroutines.cancellation.CancellationException

/**
 * 请求任务的基类
 */
internal abstract class OkHttpTask<T>(
    private val httpClient: OkHttpClient, request: eth.model.Request
) : Task<T>(request) {

    private var okHttpCall: Call? = null

    abstract val realRequest: OkRequest

    final override fun cancel() {
        okHttpCall?.cancel()
        okHttpCall = null
    }

    final override fun enqueue(): AsyncResult<T> {
        val result = AsyncResult<T>()

        val call = getCall()
        okHttpCall = call

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onCallFailure(call, e, result)
                result.onFailure?.invoke(result.exception)
                okHttpCall = null
            }

            override fun onResponse(call: Call, response: OkResponse) {
                onCallResponse(call, response, result)
                result.onSuccess?.invoke(result.data.throwIfNull("data is null"))
                response.safeClose()
                okHttpCall = null
            }
        })

        return result
    }

    final override fun execute(): SyncResult<T> {
        val result = SyncResult<T>()

        val call = getCall()
        okHttpCall = call

        try {
            // 必须close掉, 不然会内存泄露
            // A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks
            call.execute().use { response ->
                if (response.isSuccessful) {
                    onCallResponse(call, response, result)
                    okHttpCall = null
                } else {
                    val exception = EthException(
                        "${response.code}",
                        response.toString()
                    )
                    onCallFailure(call, exception, result)
                    okHttpCall = null
                }
            }
        } catch (e: Exception) {
            onCallFailure(call, e, result)
            okHttpCall = null
        }

        return result
    }

    protected fun OkRequestBuilder.requestUrl(url: String?) = self {
        url?.let { url(it) }
    }

    protected fun OkRequestBuilder.addHeaders(headers: Map<String, Any>?) = self {
        headers?.forEach { addHeader(it.key, it.value.toString()) }
    }

    private fun onCallResponse(call: Call, okResponse: OkResponse, result: Result<T>) {
        if (call.isCanceled()) {
            result.isSuccessful = false
            return
        }

        val r = Response(request, okResponse.code)
        r.body = ResponseBody()

        okResponse.body?.let { okBody ->
            val contentLength = okBody.contentLength()
            if (request.method == HttpMethod.DOWNLOAD) {
                val offset: Long = if (request.useCheckPoint) {
                    File(
                        DownloadConst.tempDir,
                        request.url.hashCode()
                            .toString()
                            .plus(DownloadConst.TEMP_FILE_SUFFIX)
                    ).length()
                } else 0L

                // 回调一次初初始化的进度
                val progress = offset.toFloat() / contentLength * 100
                progressListener?.onProgress(Progress(progress, contentLength))

                r.body?.byteStream = ProgressResponseBody(offset, okBody, progressListener).byteStream()
            } else {
                r.body?.content = okBody.string()
            }
            r.body?.contentLength = contentLength
        }
        onResponse(r, result)
    }

    private fun onCallFailure(call: Call, exception: Exception, result: Result<T>) {
        val throwable: Throwable = when (exception) {
            is SocketTimeoutException,
            is UnknownHostException,
            is ConnectException,
            is UnknownServiceException,
            is SocketException -> exception

            else -> {
                if (call.isCanceled()) {
                    CancellationException()
                } else {
                    exception
                }
            }
        }

        result.isSuccessful = false
        result.exception = throwable
    }

    private fun getCall(): Call {
        return httpClient.newCall(realRequest)
    }
}