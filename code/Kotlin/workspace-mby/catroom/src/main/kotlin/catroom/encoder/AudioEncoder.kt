package catroom.encoder

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaRecorder
import logger.L

/**
 * @author yuansui
 * @since 2024/7/13
 */
class AudioEncoder(context: Context) : BaseEncoder() {

    companion object {
        private const val LOG_TAG = "AudioEncoder"

        const val SAMPLE_RATE = 44100
        private const val BIT_RATE = 64 * 1024 // 64kbps
    }

    val channelConfig: Int
        get() = AudioFormat.CHANNEL_IN_MONO

    private val recorder = Recorder.Builder(context)
        .audioSource(MediaRecorder.AudioSource.MIC)
        .channel(channelConfig)
        .sampleRate(SAMPLE_RATE)
        .listener { data, _ ->
            encode(data)
        }.build()


    override fun onStart() {
        recorder.start()
    }

    override fun onStop() {
        recorder.stop()
    }

    override fun createMediaCodec(): MediaCodec {
        val channelCount = if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        L.d(LOG_TAG, "createMediaCodec, channelCount = $channelCount")
        return MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC).apply {
            val format =
                MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, SAMPLE_RATE, channelCount).apply {
                    setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
                    setInteger(MediaFormat.KEY_CHANNEL_MASK, channelConfig)
                    setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, recorder.bufferSize)
                    setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
                    setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount)
                }
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        }
    }

    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
        try {
            val buffer = codec.getInputBuffer(index) ?: run {
                codec.queueInputBuffer(index, 0, 0, 0, 0)
                return
            }
            buffer.clear()
            val frame = frameQueue.poll()
            if (frame == null) {
                codec.queueInputBuffer(index, 0, 0, 0, 0)
                return
            }
            buffer.put(frame.data)
            val pts = calculatePts(frame)
            codec.queueInputBuffer(index, 0, frame.data.size, pts, 0)
        } catch (e: Exception) {
            L.e(LOG_TAG, "onInputBufferAvailable", e)
            codec.queueInputBuffer(index, 0, 0, 0, 0)
        }
    }

    override fun onOutputBufferAvailable(codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo) {
//        L.d(LOG_TAG, "onOutputBufferAvailable, index = $index, info = ${info.offset}, infoSize = ${info.size}")
        try {
            val outputBuffer = codec.getOutputBuffer(index)
            outputBuffer?.let {
//                L.d(LOG_TAG, "onOutputBufferAvailable, info.presentationTimeUs = ${info.presentationTimeUs}")
                it.position(info.offset)
                it.limit(info.offset + info.size)
                val outData = ByteArray(outputBuffer.remaining())
                outputBuffer.get(outData)
                sendData(outData, info)
            }
            codec.releaseOutputBuffer(index, false)
        } catch (e: Exception) {
            L.e(LOG_TAG, "onOutputBufferAvailable", e)
        }
    }

    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        L.e(LOG_TAG, "onError", e)
    }

    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
        // do nothing
    }
}