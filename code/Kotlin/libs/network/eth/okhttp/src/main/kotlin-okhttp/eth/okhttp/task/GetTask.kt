package eth.okhttp.task

import android.net.Uri
import androidx.core.net.toUri
import eth.model.Request
import okhttp.ext.OkRequest
import okhttp.ext.OkRequestBuilder
import okhttp3.OkHttpClient

/**
 * get请求的任务
 * @author : GuoXuan
 * @since : 2019/5/31
 */
internal open class GetTask<T>(
    httpClient: OkHttpClient, request: Request
) : OkHttpTask<T>(httpClient, request) {

    private val url: String?
        get() = request.url?.toUri()
            ?.buildUpon()
            ?.appParam(request.params)
            ?.build()
            ?.toString()

    override val realRequest: OkRequest
        get() = OkRequestBuilder()
            .get()
            .requestUrl(url)
            .addHeaders(request.headers)
            .build()

    /**
     * 拼接参数
     * @param map [Request]中的[Request.params]
     * @return [Uri.Builder]
     */
    private fun Uri.Builder.appParam(map: Map<String, Any>?): Uri.Builder {
        if (map.isNullOrEmpty()) return this
        for (key in map.keys) {
            appendQueryParameter(key, map[key].toString())
        }
        return this
    }
}