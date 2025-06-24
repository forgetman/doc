package eth.convertor

import eson.Eson
import eth.def.DownloadConst
import eth.ext.downloadName
import eth.ext.paramUpperBound
import eth.model.ErrorDefaultCode
import eth.model.EthException
import eth.model.HttpMethod
import eth.model.Response
import logger.L
import sugar.ext.throwIfNull
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.absoluteValue

/**
 * @author yuansui
 * @since 2023/1/19
 */
abstract class AbstractDownloadConverter : Converter {

    companion object {
        private const val LOG_TAG = "AbstractDownloadConverter"

        private const val READ_SIZE = 1024 * 256
    }

    /**
     * 完整路径, 如: /storage/emulated/0/Android/data/包名/files
     */
    abstract fun dirName(response: Response): String

    protected open fun fileName(response: Response): String? {
        return null
    }

    /**
     * 是否删除已存在的同名文件
     */
    protected open fun deleteExistFile(response: Response): Boolean {
        return true
    }

    override fun <T> onResponse(response: Response, eson: Eson): T? {
        val method = response.request.method
        if (method != HttpMethod.DOWNLOAD) return null

        val responseType = response.request.returnType?.paramUpperBound ?: return null
        if (responseType == DownloadResult::class.java) {
            val convertResult = download(response)
            if (convertResult.success) {
                val filePath = convertResult.path.throwIfNull("download filePath is null")
                val fileName = convertResult.name.throwIfNull("download fileName is null")
                @Suppress("UNCHECKED_CAST")
                return DownloadResult(convertResult.contentLength, filePath, fileName) as T
            } else {
                throw EthException(ErrorDefaultCode.CREATE_FILE_FAIL, convertResult.msg, convertResult.error)
            }
        } else {
            throw EthException(
                ErrorDefaultCode.DOWNLOAD_RETURN_TYPE_ERROR,
                "声明的对象不是可以处理的类型, 目前只支持[eth.convertor.DownloadResult]"
            )
        }
    }

    private fun download(response: Response): DownloadConvertResult {
        val realResult = DownloadConvertResult()

        val dirName = dirName(response)
        // 创建文件夹
        val dir = File(dirName)
        if (!dir.exists() && !dir.mkdirs()) {
            throw EthException(ErrorDefaultCode.CREATE_FILE_FAIL, "路径不存在且无法创建: $dir")
        }

        // 删除原文件
        val fileName = fileName(response) ?: response.request.url.downloadName
        L.d(LOG_TAG, "download, fileName = $fileName")
        if (fileName.isEmpty()) return kotlin.run {
            realResult.msg = "没有设置[fileName]属性, 且无法根据url解析出合适的文件名"
            realResult
        }

        val saveFile = File(dir, fileName)
        if (saveFile.exists()) {
            if (deleteExistFile(response)) {
                saveFile.delete()
            } else {
                realResult.msg = "已存在同名文件"
                return realResult
            }
        }

        val tempFile = File(
            DownloadConst.tempDir,
            response.request.url
                .hashCode().absoluteValue
                .toString()
                .plus(DownloadConst.TEMP_FILE_SUFFIX)
        )
        L.d(LOG_TAG, "download, tempFile = $tempFile")
        val useCheckPoint = response.request.useCheckPoint
        if (tempFile.exists() && !useCheckPoint) {
            // 不使用断点续传的情况下, 删除已有的临时文件
            tempFile.delete()
        } else {
            val parent = tempFile.parentFile
            if (parent?.exists() == false && !parent.mkdirs()) {
                throw EthException(ErrorDefaultCode.CREATE_FILE_FAIL, "路径不存在且无法创建: $parent")
            }
        }

        try {
            val seekLength = if (useCheckPoint) {
                tempFile.length()
            } else {
                0L
            }

            response.body?.byteStream?.use { stream ->
                RandomAccessFile(tempFile, "rw").use { accessFile ->
                    L.d(LOG_TAG, "download, seekLength = $seekLength")
                    accessFile.seek(seekLength)

                    val bytes = ByteArray(READ_SIZE)
                    var len = stream.read(bytes)
                    while (len != -1) {
                        accessFile.write(bytes, 0, len)
                        len = stream.read(bytes)
                    }
                }
            }

            // 下载成功再改名字
            val renameResult = tempFile.renameTo(saveFile)
            if (renameResult) {
                realResult.success = true
                realResult.contentLength = (response.body?.contentLength ?: 0L) + seekLength
                realResult.path = dirName
                realResult.name = fileName
            } else {
                realResult.msg = "文件改名失败"
            }
        } catch (e: Throwable) {
            realResult.error = e
            realResult.msg = "下载失败"
        }
        return realResult
    }
}