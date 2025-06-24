package eth.okhttp.task

import eth.def.DownloadConst
import eth.def.HeaderKey
import okhttp.ext.OkRequest
import okhttp.ext.OkRequestBuilder
import okhttp3.OkHttpClient
import java.io.File

/**
 * download请求的任务
 * @author : GuoXuan
 * @since : 2019/5/31
 */
internal class DownloadTask<T>(
    httpClient: OkHttpClient, request: eth.model.Request
) : GetTask<T>(httpClient, request) {

    companion object {
        private const val RANGE_START = "bytes="
        private const val RANGE_END = "-"
    }

    private val headers: Map<String, Any>
        get() {
            val map = HashMap<String, Any>()
            request.headers?.let { oldHeaders ->
                map.putAll(oldHeaders)
            }
            if (request.useCheckPoint) {
                val tempFile = File(
                    DownloadConst.tempDir,
                    request.url.hashCode()
                        .toString()
                        .plus(DownloadConst.TEMP_FILE_SUFFIX)
                )
                if (tempFile.exists()) {
                    // "bytes=1000-"
                    map[HeaderKey.RANGE] = "$RANGE_START${tempFile.length()}$RANGE_END"
                }
            }
            return map
        }

    override val realRequest: OkRequest
        get() = OkRequestBuilder()
            .get()
            .requestUrl(request.url)
            .addHeaders(headers)
            .build()
}