package eth.okhttp.task

import eth.okhttp.body.ProgressRequestBody
import okhttp.ext.OkRequestBuilder
import okhttp3.OkHttpClient

/**
 * post请求的任务
 */
internal class DeleteTask<T>(
    httpClient: OkHttpClient, request: eth.model.Request
) : PostTask<T>(httpClient, request) {

    override fun OkRequestBuilder.method(progressBody: ProgressRequestBody) {
        delete(progressBody)
    }

}