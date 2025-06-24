package eth.okhttp.body

import eth.Task
import eth.model.Progress
import logger.L
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * 带进度的RequestBody
 */
class ProgressRequestBody(
    private val body: RequestBody,
    private val listener: Task.ProgressListener? = null
) : RequestBody() {

    private var time: Long = 0L
    private lateinit var bufferedSink: BufferedSink

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    override fun contentType(): MediaType? {
        return body.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun contentLength(): Long {
        return body.contentLength()
    }

    /**
     * 重写进行写入
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        try {
            //包装
            bufferedSink = sink(sink).buffer()
            //写入
            body.writeTo(bufferedSink)
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush()
        } catch (e: Exception) {
            L.e("ProgressRequestBody writeTo exception: ${e.message}")
        }
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten: Long = 0

            //总字节长度，避免多次调用contentLength()方法
            var contentLength: Long = 0

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)

                if (contentLength == 0L) {
                    contentLength = contentLength()
                }

                //增加当前写入的字节数
                bytesWritten += byteCount

                //回调
                val curr = System.currentTimeMillis()
                if (time == 0L || curr - time > 500 || bytesWritten >= contentLength) {
                    time = curr

                    if (listener != null) {
                        val progress = bytesWritten.toFloat() / contentLength * 100
                        listener.onProgress(Progress(progress, contentLength))
                    }
                }
            }
        }
    }
}