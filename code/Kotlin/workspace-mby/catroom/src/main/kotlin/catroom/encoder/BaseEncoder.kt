package catroom.encoder

import android.media.MediaCodec
import android.os.Handler
import android.os.HandlerThread
import sugar.collection.safeMutableListOf
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.reflect.jvm.jvmName

data class Frame(val data: ByteArray, val timestamp: Long)

abstract class BaseEncoder : MediaCodec.Callback() {
    fun interface Listener {
        fun onData(data: ByteArray, info: MediaCodec.BufferInfo)
    }

    private val listeners = safeMutableListOf<Listener>()

    private var codec: MediaCodec? = null
    private var codecThread: HandlerThread? = null
    private var codecHandler: Handler? = null

    private var isRunning = false
    protected val frameQueue: BlockingQueue<Frame> = LinkedBlockingQueue()

    protected var presentTimeUs = 0L

    protected abstract fun createMediaCodec(): MediaCodec

    fun start() {
        if (isRunning) return
        isRunning = true

        presentTimeUs = System.nanoTime() / 1000

        val newCodec = createMediaCodec()
        onMediaCodecCreated(newCodec)
        if (isSdkAtLeast(SdkInt.M_23)) {
            val thread = HandlerThread(this::class.jvmName)
            thread.start()
            val handler = Handler(thread.looper)
            newCodec.setCallback(this, handler)

            codecHandler = handler
            codecThread = thread
        } else {
            newCodec.setCallback(this)
        }
        newCodec.start()
        codec = newCodec

        onStart()
    }

    protected open fun onStart() {}

    protected open fun onMediaCodecCreated(mediaCodec: MediaCodec) {}

    fun encode(input: ByteArray) {
        val time = System.nanoTime() / 1000
        frameQueue.offer(Frame(input, time))
    }

    fun addListener(listener: Listener) {
        if (listeners.contains(listener)) return
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    protected fun sendData(data: ByteArray, info: MediaCodec.BufferInfo) {
        listeners.forEachElement { it.onData(data, info) }
    }

    fun stop(callback: (() -> Unit)? = null) {
        if (!isRunning) return
        isRunning = false

        presentTimeUs = 0L

        fun release() {
            codec?.apply {
                stop()
                this.release()
            }
            codec = null

            onStop()
            callback?.invoke()
        }

        if (isSdkAtLeast(SdkInt.M_23)) {
            codecHandler?.let {
                it.postDelayed({
                    release()

                    codecThread?.quitSafely()
                    codecHandler = null
                    codecThread = null
                }, 1000)
            } ?: run {
                release()

            }
        } else {
            release()
        }
    }

    protected open fun onStop() {}

    protected fun maybeRestart() {
        if (isRunning) {
            stop {
                start()
            }
        }
    }

    protected fun getMediaCodec(): MediaCodec {
        return codec ?: throw IllegalStateException("MediaCodec is null")
    }

    protected fun calculatePts(frame: Frame): Long {
        return max(0, frame.timestamp - presentTimeUs)
    }
}