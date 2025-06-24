package eth.okhttp.task

import okhttp.ext.OkRequestBuilder
import eth.okhttp.body.ProgressRequestBody
import okhttp3.OkHttpClient

/**
 * post请求的任务
 * @author : GuoXuan
 * @since : 2019/5/31
 */
internal class PutTask<T>(
    httpClient: OkHttpClient, request: eth.model.Request
) : PostTask<T>(httpClient, request) {

    override fun OkRequestBuilder.method(progressBody: ProgressRequestBody) {
        put(progressBody)
    }

}