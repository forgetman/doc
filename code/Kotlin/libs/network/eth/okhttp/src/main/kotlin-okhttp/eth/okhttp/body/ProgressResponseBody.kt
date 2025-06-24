package eth.okhttp.body

import eth.Task
import eth.model.Progress
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * 带进度的ResponseBody
 */
class ProgressResponseBody(
    private val readOffset: Long,
    private val body: ResponseBody,
    private val listener: Task.ProgressListener? = null
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null
    private var time: Long = 0L

    override fun contentType(): MediaType? {
        return body.contentType()
    }

    override fun contentLength(): Long {
        return body.contentLength()
    }

    override fun source(): BufferedSource {
        return bufferedSource ?: source(body.source()).buffer().let {
            bufferedSource = it
            it
        }
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            //当前读取字节数
            var bytesRead = readOffset
            val contentLength = contentLength() + readOffset
            var lastProgress = 0f

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {

                val bytes = super.read(sink, byteCount)
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                this.bytesRead += if (bytes != -1L) bytes else 0

                //回调
                val curr = System.currentTimeMillis()
                if (time == 0L || curr - time > 500 || bytesRead >= contentLength) {
                    time = curr

                    if (listener != null) {
                        val progress = bytesRead.toFloat() / contentLength * 100
                        if (lastProgress != progress) {
                            lastProgress = progress
                            listener.onProgress(Progress(progress, contentLength))
                        }
                    }
                }

                return bytes
            }
        }
    }
}