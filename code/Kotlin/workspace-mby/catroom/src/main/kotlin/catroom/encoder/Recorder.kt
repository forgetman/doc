package catroom.encoder

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import catroom.BuildConfig
import catroom.util.WavUtil
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import logger.L
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.safeClose
import sugar.ext.self
import vector.util.DangerousPerm
import vector.util.Dir
import vector.util.EasyPermissions
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * @param audioSource [MediaRecorder.AudioSource.DEFAULT]/[MediaRecorder.AudioSource.MIC]/[MediaRecorder.AudioSource.CAMCORDER]
 */
class Recorder private constructor(
    private val context: Context,
    private val sampleRate: Int,
    private val channelConfig: Int,
    private val audioSource: Int,
    private val listener: Listener?
) {

    companion object {
        private const val LOG_TAG = "Recorder"

        private const val SAMPLE_RATE: Int = 44100 //  采集样本频率
        private const val AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT // 每个采用点占用16位（2个字节）
    }

    fun interface Listener {
        fun onPcmData(data: ByteArray, lengthOrCode: Int)
    }

    private var isRunning = false
    private var recordJob: Job? = null

    val bufferSize: Int = AudioRecord.getMinBufferSize(
        sampleRate,
        channelConfig,
        AUDIO_ENCODING
    )

    private val wavFile = File(
        Dir.External.getFileDir(Environment.DIRECTORY_MOVIES),
        "recorder.wav"
    ).apply {
        if (exists()) {
            delete()
        }
    }
    private val outputStream by lazy { wavFile.outputStream() }
    private val saveToSdcard = BuildConfig.OUTPUT_TO_SDCARD

    class Builder(private val context: Context) {
        private var sampleRate: Int = SAMPLE_RATE
        private var channel: Int = AUDIO_CHANNEL
        private var audioSource: Int = MediaRecorder.AudioSource.DEFAULT
        private var listener: Listener? = null

        fun sampleRate(sampleRate: Int) = self { this.sampleRate = sampleRate }
        fun channel(channel: Int) = self { this.channel = channel }
        fun audioSource(audioSource: Int) = self { this.audioSource = audioSource }
        fun listener(listener: Listener) = self { this.listener = listener }

        fun build(): Recorder {
            return Recorder(context, sampleRate, channel, audioSource, listener)
        }
    }

    @SuppressLint("MissingPermission")
    fun start(): Boolean {
        L.d(LOG_TAG, "start")
        if (isRunning) return false
        val hasPermission = EasyPermissions.check(context, DangerousPerm.Microphone())
        if (!hasPermission) return false

        var audioRecord: AudioRecord? = null

        recordJob = callbackFlow {
            val record = if (isSdkAtLeast(SdkInt.M_23)) {
                val format = AudioFormat.Builder()
                    .setEncoding(AUDIO_ENCODING)
                    .setSampleRate(sampleRate)
                    .setChannelMask(this@Recorder.channelConfig)
                    .build()
                AudioRecord.Builder()
                    .setAudioSource(audioSource)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(bufferSize)
                    .build()
            } else {
                AudioRecord(
                    audioSource,
                    sampleRate,
                    this@Recorder.channelConfig,
                    AUDIO_ENCODING,
                    bufferSize
                )
            }

            if (record.state != AudioRecord.STATE_INITIALIZED) throw IllegalStateException("Some parameters specified is not valid")
            audioRecord = record

            record.startRecording()

            isRunning = true

            val data = ByteArray(bufferSize)
            while (isRunning) {
                when (val lengthOrCode = record.read(data, 0, data.size)) {
                    AudioRecord.ERROR_BAD_VALUE,
                    AudioRecord.ERROR_DEAD_OBJECT,
                    AudioRecord.ERROR,
                    AudioRecord.ERROR_INVALID_OPERATION -> {
                        throw IllegalStateException("sys recorder error = $lengthOrCode")
                    }

                    0 -> {
                        throw IllegalStateException("sys recorder read length = 0")
                    }

                    else -> {
                        trySend(Pair(data, lengthOrCode))
                    }
                }
            }

            awaitClose {
                L.d(LOG_TAG, "awaitClose")
                record.stop()
                record.release()
            }
        }.retryWhen { cause, attempt ->
            L.i(LOG_TAG, "Audio record retry, cause = $cause, attempt = $attempt")
            audioRecord?.let { record ->
                record.stop()
                record.release()
            }
            delay(2000)
            true
        }.onEach { (data, lengthOrCode) ->
//            L.d(LOG_TAG, "on data size = $lengthOrCode")
            if (saveToSdcard) {
                outputStream.write(data)
            }
            listener?.onPcmData(data, lengthOrCode)
        }.onCompletion {
            if (saveToSdcard) {
                outputStream.safeClose()
                writeWavHeader(wavFile)
            }
        }.catch { e ->
            L.e(LOG_TAG, "Audio record catch error = $e")
        }.flowOn(Dispatchers.IO).launchForever()

        return true
    }

    fun stop(): Boolean {
        L.d(LOG_TAG, "stop")
        if (!isRunning) return false
        isRunning = false
        stopRecord()
        return true
    }

    private fun stopRecord() {
        recordJob?.cancel()
        recordJob = null
    }

    /**
     * 写入wav文件头
     */
    @Throws(IOException::class)
    fun writeWavHeader(file: File) {
        val wavFile = RandomAccessFile(file, "rw")
        wavFile.seek(0) // to the beginning
        wavFile.write(getHeader(file))
        wavFile.safeClose()
    }

    private fun getHeader(file: File): ByteArray {
        return WavUtil.createHeader(
            file.length().toInt(),
            if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2,
            SAMPLE_RATE,
            AUDIO_ENCODING
        )
    }
}