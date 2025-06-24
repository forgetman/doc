package com.tencent.iot.explorer.device.android.http.retrofit.converter


import com.tencent.iot.explorer.device.android.utils.TXLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import java.io.IOException

/**
 * 自定义RequestBodyConverter
 */
class StringRequestBodyConverter : Converter<String, RequestBody> {

    @Throws(IOException::class)
    override fun convert(s: String): RequestBody {
        TXLog.e(TAG, "请求数据json：$s")
        return s.toRequestBody(MEDIA_TYPE)
    }

    companion object {
        private const val TAG = "StringRequestBodyConverter"
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    }
}
