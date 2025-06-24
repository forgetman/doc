package logger.api

import java.util.Date

interface Uploader {

    fun interface ResultListener {
        fun onUploadResult(result: Boolean)
    }

    /**
     * 上传日志(可选)
     */
    fun upload(date: Date?, resultListener: ResultListener? = null)

    /**
     * 上传所有(可选)
     */
    fun uploadAll(resultListener: ResultListener? = null)
}
