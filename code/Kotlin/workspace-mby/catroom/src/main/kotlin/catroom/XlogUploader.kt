package catroom

import catroom.datastore.Properties
import catroom.network.api.RoomApi
import catroom.network.createApi
import coroutine.flow.launchForever
import eth.ext.asProgressFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import logger.api.Uploader
import vector.ext.isNotNullOrEmpty
import vector.util.DeviceIdUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author yuansui
 * @since 2024/8/29
 */
class XlogUploader(private val logPath: String) : Uploader {

    companion object {
        private const val LOG_TAG = "XlogUploader"
        private const val LOG_FILE_SUFFIX = ".xlog"
    }

    // 有时候传log会比较慢, 防止重复上传
    private val uploadJobMap = mutableMapOf<String, Job>()

    override fun upload(date: Date?, resultListener: Uploader.ResultListener?) {
        val dateFormat = SimpleDateFormat("_yyyyMMdd", Locale.CHINA)
        val dateStr = dateFormat.format(date ?: Date())
        val file = File(logPath, dateStr + LOG_FILE_SUFFIX)
        if (!file.exists()) return
        L.d(LOG_TAG, "upload log, file = $file")
        upload(file, resultListener)
    }

    override fun uploadAll(resultListener: Uploader.ResultListener?) {
        val dirFile = File(logPath)
        L.d(LOG_TAG, "upload, filePath = $dirFile")
        callbackFlow {
            val result = suspendCoroutine { cont ->
                var finishSize = 0
                var successSize = 0
                val files = dirFile.listFiles()?.filter { it.path.endsWith(LOG_FILE_SUFFIX) }
                val size = files?.size ?: 0
                files?.forEach { file ->
                    L.d(LOG_TAG, "uploadAll, file = $file")
                    upload(file) {
                        if (it) successSize++
                        finishSize++
                        if (finishSize == size) {
                            cont.resume(successSize == size)
                        }
                    }
                }
            }
            trySend(result)
            close()
        }.onEach {
            L.d(LOG_TAG, "uploadAll, result = $it")
            resultListener?.onUploadResult(it)
        }.catch { e ->
            L.e(LOG_TAG, "uploadAll", e)
            resultListener?.onUploadResult(false)
        }.launchForever()
    }

    private fun upload(file: File, resultListener: Uploader.ResultListener?) {
        uploadJobMap[file.name]?.cancel()
        uploadJobMap[file.name] = flow {
            val savedRoomName = Properties.roomName.getOrNull()
            val roomName = if (savedRoomName.isNotNullOrEmpty()) {
                "($savedRoomName)"
            } else ""
            emit(roomName)
        }.flatMapConcat { roomName ->
            createApi<RoomApi>().uploadLog(
                DeviceIdUtil.id + roomName,
                file.absolutePath
            ).asProgressFlow().onProgress { progress ->
                L.d(LOG_TAG, "upload, progress = ${progress.progress}")
            }.flowOn(Dispatchers.IO).onEach {
                L.d(LOG_TAG, "upload log, ${file.name} complete")
                uploadJobMap.remove(file.name)
                resultListener?.onUploadResult(true)
            }.catch { e ->
                L.e(LOG_TAG, "upload log ${file.name}", e)
                uploadJobMap.remove(file.name)
                resultListener?.onUploadResult(false)
            }
        }.launchForever()
    }
}