package catroom.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import androidx.core.os.bundleOf
import catroom.BuildConfig
import catroom.def.Resolution
import catroom.encoder.VideoEncoder.Type.*
import logger.L
import sugar.ext.safeClose
import sugar.ext.self
import sugar.ext.throwIfNull
import vector.os.Size
import vector.util.Dir
import java.io.File
import java.nio.ByteBuffer
import kotlin.to

class VideoEncoder private constructor(
    private val outputSize: Size,
    val bitrate: Int,
    val bitrateMode: Int?,
    private val frameRate: Int,
    val type: Type,
    private val vendorId: Int,
    private val listener: Listener
) : BaseEncoder() {
    companion object {
        private const val LOG_TAG = "VideoEncoder"
    }

    enum class Type(val mine: String) {
        H264(MediaFormat.MIMETYPE_VIDEO_AVC),
        H265(MediaFormat.MIMETYPE_VIDEO_HEVC),
    }

    interface Listener {
        fun onVideoInfo(sps: ByteBuffer, pps: ByteBuffer?, vps: ByteBuffer?)
    }

    class Builder(encoder: VideoEncoder?) {
        constructor() : this(null)

        private var outputSize: Size = Size(Resolution.P480.width, Resolution.P480.height)
        private var bitrate: Int? = null
        private var bitrateMode: Int? = null
        private var frameRate: Int = 30
        private var type: Type = Type.H264
        private var vendorId: Int? = null
        private var listener: Listener? = null

        init {
            encoder?.let {
                outputSize = it.outputSize
                bitrate = it.bitrate
                bitrateMode = it.bitrateMode
                frameRate = it.frameRate
                type = it.type
                vendorId = it.vendorId
            }
        }

        fun outputSize(outputSize: Size) = self { this.outputSize = outputSize }
        fun bitrate(bitrate: Int) = self { this.bitrate = bitrate }
        fun bitrateMode(bitRateMode: Int) = self { this.bitrateMode = bitRateMode }
        fun frameRate(frameRate: Int) = self { this.frameRate = frameRate }
        fun type(type: Type) = self { this.type = type }
        fun vendorId(vendorId: Int) = self { this.vendorId = vendorId }
        fun listener(listener: Listener) = self { this.listener = listener }

        fun build() = VideoEncoder(
            outputSize,
            bitrate ?: (0.1f * outputSize.width * outputSize.height).toInt(),
            bitrateMode,
            frameRate,
            type,
            vendorId.throwIfNull("vendorId must not be null"),
            listener.throwIfNull("listener must not be null")
        )
    }

    private val h264File = File(
        Dir.External.getFileDir(Environment.DIRECTORY_MOVIES),
        "h264-$vendorId.mp4"
    ).apply {
        if (exists()) {
            delete()
        }
    }
    private val outputStream264 by lazy { h264File.outputStream() }
    private val saveToSdcard = BuildConfig.OUTPUT_TO_SDCARD

    init {
        L.d(LOG_TAG, "outputSize = $outputSize, bitrate = $bitrate, frameRate = $frameRate, type = $type")
    }

    override fun createMediaCodec(): MediaCodec {
        return MediaCodec.createEncoderByType(type.mine).apply {
            val format = MediaFormat.createVideoFormat(type.mine, outputSize.width, outputSize.height).apply {
                setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
                setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
//                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0)
                bitrateMode?.let {
                    if (isBitrateModeSupported(bitrateMode, codecInfo, type.mine)) {
                        setInteger(MediaFormat.KEY_BITRATE_MODE, bitrateMode)
                    }
                }
            }
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            setParameters(bundleOf(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME to 0))
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
//         L.d(LOG_TAG, "onOutputBufferAvailable, index = $index, info = ${info.offset}, infoSize = ${info.size}")
        try {
            val outputBuffer = codec.getOutputBuffer(index)
            outputBuffer?.let {
                it.position(info.offset)
                it.limit(info.offset + info.size)
                val outData = ByteArray(outputBuffer.remaining())
                outputBuffer.get(outData)
                if (saveToSdcard) outputStream264.write(outData)
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
        when (type) {
            H264 -> {
                val sps = format.getByteBuffer("csd-0").throwIfNull("sps must not be null")
                val pps = format.getByteBuffer("csd-1")
                val vps = null
                listener.onVideoInfo(sps, pps, vps)
            }

            H265 -> {
                val byteBufferList = extractVpsSpsPpsFromH265(
                    format.getByteBuffer("csd-0").throwIfNull("csd-0 must not be null")
                )
                if (byteBufferList.size == 3) {
                    L.d(LOG_TAG, "$vendorId manual vps/sps/pps extraction success")
                    val sps = byteBufferList[1]
                    val pps = byteBufferList[2]
                    val vps = byteBufferList[0]
                    listener.onVideoInfo(sps, pps, vps)
                } else {
                    L.d(LOG_TAG, "$vendorId manual vps/sps/pps extraction failed")
                }
            }
        }
    }

    override fun onStop() {
        if (saveToSdcard) outputStream264.safeClose()
    }

    private fun isBitrateModeSupported(mode: Int, mediaCodecInfo: MediaCodecInfo, mime: String): Boolean {
        val codecCapabilities = mediaCodecInfo.getCapabilitiesForType(mime)
        val encoderCapabilities = codecCapabilities.encoderCapabilities
        return encoderCapabilities.isBitrateModeSupported(mode)
    }

    /**
     * You need find 0 0 0 1 byte sequence that is the initiation of vps, sps and pps
     * buffers.
     *
     * @param csd0byteBuffer get in mediacodec case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED
     * @return list with vps, sps and pps
     */
    private fun extractVpsSpsPpsFromH265(csd0byteBuffer: ByteBuffer): List<ByteBuffer> {
        val byteBufferList: MutableList<ByteBuffer> = ArrayList()
        var vpsPosition = -1
        var spsPosition = -1
        var ppsPosition = -1
        var contBufferInitiation = 0
        val length = csd0byteBuffer.remaining()
        val csdArray = ByteArray(length)
        csd0byteBuffer[csdArray, 0, length]
        csd0byteBuffer.rewind()
        for (i in csdArray.indices) {
            if (contBufferInitiation == 3 && csdArray[i].toInt() == 1) {
                if (vpsPosition == -1) {
                    vpsPosition = i - 3
                } else if (spsPosition == -1) {
                    spsPosition = i - 3
                } else {
                    ppsPosition = i - 3
                }
            }
            if (csdArray[i].toInt() == 0) {
                contBufferInitiation++
            } else {
                contBufferInitiation = 0
            }
        }
        val vps = ByteArray(spsPosition)
        val sps = ByteArray(ppsPosition - spsPosition)
        val pps = ByteArray(csdArray.size - ppsPosition)
        for (i in csdArray.indices) {
            if (i < spsPosition) {
                vps[i] = csdArray[i]
            } else if (i < ppsPosition) {
                sps[i - spsPosition] = csdArray[i]
            } else {
                pps[i - ppsPosition] = csdArray[i]
            }
        }
        byteBufferList.add(ByteBuffer.wrap(vps))
        byteBufferList.add(ByteBuffer.wrap(sps))
        byteBufferList.add(ByteBuffer.wrap(pps))
        return byteBufferList
    }
}