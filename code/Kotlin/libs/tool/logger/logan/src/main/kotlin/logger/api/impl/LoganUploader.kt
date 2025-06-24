package logger.api.impl

import android.util.Log
import com.dianping.logan.Logan
import logger.L
import logger.api.LoggerConfig
import logger.api.Uploader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoganUploader(
    private val config: LoggerConfig,
    private val deviceId: String?,
    private val url: String
) : Uploader {

    companion object {
        private const val LOG_TAG = "LoganUploader"

        private const val DATE_FORMAT = "yyyy-MM-dd"

        private const val HEADER_DEVICE_ID = "deviceId"
        private const val HEADER_VERSION_CODE = "versionCode"
        private const val HEADER_VERSION_NAME = "versionName"

        private const val UPLOAD_SUCCESS = 200

    }

    fun uploadByDate(log: Pair<Long, String>, result: Uploader.ResultListener?) {
        val (timestamp, date) = log
        Log.d(LOG_TAG, "开始日志上传, date = $date, timestamp = $timestamp")
        Logan.s(
            url,
            date,
            generateHeaders()
        ) { statusCode, data: ByteArray? ->
            Log.d(
                LOG_TAG,
                "日志上传结束, date = $date, code = $statusCode, data = ${data.toResult()}"
            )
            if (statusCode == UPLOAD_SUCCESS) {
                val logFile = File(config.cachePath(), timestamp.toString())
                try {
                    val delResult = logFile.delete()
                    Log.d(LOG_TAG, "删除日志, date = $date, file = $logFile, result = $delResult")
                } catch (ex: Exception) {
                    Log.d(LOG_TAG, "删除日志失败, date = $date, file = $logFile", ex)
                }
                result?.onUploadResult(true)
            } else {
                result?.onUploadResult(false)
            }
        }
    }

    inner class Upload(val result: Uploader.ResultListener?) {

        var totalCount = 0
        var count = 0
        var resultData = true

        fun upload(dates: List<Pair<Long, String>>) {
            if (dates.isEmpty()) {
                result?.onUploadResult(true)
                return
            }
            totalCount = dates.size
            L.d(LOG_TAG, "所有日志上传, totalCount = $totalCount")
            dates.sortedByDescending {
                it.first
            }.forEach { log ->
                uploadByDate(log) {
                    count++
                    resultData = it and resultData
                    L.d(LOG_TAG, "所有日志上传, count = $count, it: $it, result: $resultData")
                    if (count == totalCount) {
                        result?.onUploadResult(resultData)
                    }
                }
            }
        }
    }

    override fun upload(date: Date?, resultListener: Uploader.ResultListener?) {
        val useDate = date ?: Date()
        val format = SimpleDateFormat(DATE_FORMAT, Locale.CHINA)
        val strDate = format.format(useDate)
        Logan.f()
        uploadByDate(Pair(format.parse(strDate)!!.time, strDate), resultListener)
    }

    override fun uploadAll(resultListener: Uploader.ResultListener?) {
        val logMap = Logan.getAllFilesInfo() //key是文件日期，value为文件大小
        Logan.f()
        // key是文件日期，2023-01-30
        // value是timestamp
        Upload(resultListener).upload(logMap.map { (date, _) ->
            Pair(SimpleDateFormat(DATE_FORMAT, Locale.CHINA).parse(date)!!.time, date)
        })
    }


    /**
     * 获取统一的header map
     * 所有header拼接参数来自[Logan]内部定义
     */
    private fun generateHeaders(): Map<String, String> {
        return HashMap<String, String>().apply {
            put(HEADER_DEVICE_ID, deviceId ?: "unknow device id")
            put(HEADER_VERSION_CODE, config.versionCode())
            put(HEADER_VERSION_NAME, config.versionName())
        }
    }

    private fun ByteArray?.toResult(): String {
        return if (this != null) String(this) else "data is null"
    }
}