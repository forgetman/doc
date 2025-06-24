package feature.tts.engine.azure

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.microsoft.cognitiveservices.speech.AudioDataStream
import com.microsoft.cognitiveservices.speech.Connection
import com.microsoft.cognitiveservices.speech.ConnectionEventArgs
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult
import com.microsoft.cognitiveservices.speech.SpeechSynthesisWordBoundaryEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import compat.network.NetworkCompat
import feature.media.ext.interruptNotificationAudioAttributes
import feature.media.player.Player
import feature.tts.def.SpeechOpCallback
import feature.tts.engine.BaseTextToSpeechEngine
import feature.tts.engine.azure.def.Constants
import feature.tts.ext.readAll
import feature.tts.model.SpeechItem
import feature.tts.processor.VolumeProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import logger.L
import tool.trigger.Trigger
import tool.trigger.strategy.BackoffStrategy
import vector.ext.subBytes
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import androidx.media3.common.Player as Media3Player

class AzureTextToSpeechEngine(context: Context) : BaseTextToSpeechEngine(context) {

    companion object {
        private val SSML_TEMPLATE = """
            <speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis"
               xmlns:mstts="https://www.w3.org/2001/mstts" xml:lang="zh-CN">
                <voice name="%s">
                    <mstts:silence type="leading-exact" value="%dms"/>
                    <mstts:silence type="tailing-exact" value="0ms"/>
                    <prosody rate="%s" pitch="%s" volume="%s">
                        <mstts:express-as style="%s">%s</mstts:express-as>
                    </prosody>
                </voice>
            </speak>
            """.trimIndent()

        private const val SPEAKER_PREFIX = "azure#"
    }

    private data class CachedResult(
        val result: SpeechSynthesisResult? = null
    )

    private val volumeProcessor = VolumeProcessor()
    private val player by lazy { createPlayer() }
    private var currPlayingId: String? = null
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var connection: Connection? = null
    private var speechConfig: SpeechConfig? = null
    private var synthesizer: SpeechSynthesizer? = null
    private var isConnected = false

    private var byteCache: ByteArray? = null

    private val resultIdMap by lazy { ConcurrentHashMap<String, String>() }
    private val elapsedMap by lazy { ConcurrentHashMap<String, Long>() }
    private val cachedTtsResults by lazy { ConcurrentHashMap<String, CachedResult>() }

    private val trigger = Trigger(context) {
        setTag(tag)
        applyStrategy(BackoffStrategy.Builder().tag(tag).build())
    }
    private val triggerListener = Trigger.Listener {
        connect()
    }

    private val mediaSourceFactory = ProgressiveMediaSource.Factory {
        AzureDataSource()
    }

    override fun onInit(): Boolean {
        initAzure()
        return true
    }

    override fun onDeinit() {
        trigger.removeListener(triggerListener)
        trigger.reset()

        try {
            //如果这时没执行完所有下载音频的任务，这里的close会报错，先catch住，然后执行完有回调后判断链路是否已销毁而去销毁自身
            //Cannot dispose a synthesizer while async synthesis is running. Await async synthesis to avoid unexpected disposals.
            synthesizer?.close()
            speechConfig?.close()
            connection?.close()
        } catch (e: Exception) {
            L.d(feature.tts.def.Constants.TTS_LOG_TAG, "$tag destroy synthesizer error = ${e.message}")
        }

        synthesizer = null
    }

    override fun onDestroy() {
        mainScope.launch {
            player.release()
            cancel()
        }
    }

    override fun onStart(item: SpeechItem, callback: SpeechOpCallback) {
        L.d("$tag onStart, item = $item")
        val params = item.params

        volumeProcessor.gain = params.volumeGain

        val uri = buildString {
            append("azure-tts://speak?")
            append("utteranceId=${item.utteranceId}")
            val speaker = params.speaker ?: run {
                Constants.SPEAKER
            }
            append("&name=$speaker")

            val allText = item.inputStream.readAll()
            append("&text=${URLEncoder.encode(allText, "UTF-8")}")

            append("&rate=${URLEncoder.encode("%.2f".format(params.speed), "UTF-8")}")
            append("&pitch=${URLEncoder.encode("${if (params.pitch >= 0) "+" else ""}${params.pitch}Hz", "UTF-8")}")
            append("&style=${params.style.ifBlank { "chat" }}")
            append("&volumeGain=${params.volumeGain}")
        }
        L.d("$tag onStart, uri: $uri")

        mainScope.launch {
            with(player) {
                setMediaItem(MediaItem.fromUri(uri))
                addListener(object : Media3Player.Listener {

                    override fun onPlayerError(error: PlaybackException) {
                        L.d("$tag onPlayerError: ${error.message}")
                        player.removeListener(this)
                        currPlayingId = null
                        callback.onResult(false)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        L.d("$tag onPlaybackStateChanged, $playbackState")

                        when (playbackState) {
                            Media3Player.STATE_READY -> {
                                currPlayingId = item.utteranceId
                            }

                            Media3Player.STATE_ENDED -> {
                                player.removeListener(this)
                                currPlayingId = null
                                callback.onResult(true)
                            }

                            else -> Unit
                        }
                    }
                })
                volume = params.volume
                prepare()
                playWhenReady = true
            }
        }
    }

    override fun onCancel(item: SpeechItem, callback: SpeechOpCallback) {
        if (item.utteranceId == currPlayingId) {
            currPlayingId = null
            mainScope.launch {
                player.stop()
            }
            callback.onResult(true)
        } else {
            callback.onResult(false)
        }
    }

    private fun initAzure() {
        if (synthesizer != null) return

        val key = context.getString(R.string.azure_key)
        val location = context.getString(R.string.azure_location)
        speechConfig = SpeechConfig.fromSubscription(key, location).apply {
            // Use 24k Hz format for higher quality.
            setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio24Khz48KBitRateMonoMp3)
            // 设置azure log路径
            //                    setProperty(
            //                        PropertyId.Speech_LogFilename,
            //                        File(JosApp.context.externalCacheDir, "azure.log").absolutePath
            //                    )
        }

        //这里会报错
        synthesizer = SpeechSynthesizer(speechConfig, null).apply {
            /**
             * 正常：SynthesisStarted -> Synthesizing -> SynthesisCompleted
             * 异常：SynthesisStarted -> SynthesisCanceled
             */
            SynthesisStarted.addEventListener { _: Any?, e: SpeechSynthesisEventArgs ->
                synchronized(resultIdMap) {
                    val utteranceId = resultIdMap[e.result.resultId]
                    L.d("$tag tts started, result id: ${e.result.resultId}, utteranceId: $utteranceId")
                }

                e.close()
            }

            Synthesizing.addEventListener { _: Any?, e: SpeechSynthesisEventArgs ->
                L.d("$tag tts synthesizing, result id: ${e.result.resultId}")
                if (elapsedMap.containsKey(e.result.resultId)) {
                    calculateElapsedTime(e.result.resultId)
                }
                e.close()
            }

            WordBoundary.addEventListener { _: Any, wordBoundary: SpeechSynthesisWordBoundaryEventArgs ->
                L.groupBy(
                    "$tag synthesis word boundary, word = ${wordBoundary.text}",
                    "audioOffset = ${wordBoundary.audioOffset}",
                    "duration = ${wordBoundary.duration}",
                    "textOffset = ${wordBoundary.textOffset}",
                    "wordLength = ${wordBoundary.wordLength}",
                    "boundaryType = ${wordBoundary.boundaryType}"
                ).d(feature.tts.def.Constants.TTS_LOG_TAG)
            }

            SynthesisCompleted.addEventListener { _: Any?, e: SpeechSynthesisEventArgs ->
                val utteranceId = resultIdMap.remove(e.result.resultId)
                L.d("$tag tts synthesis completed, result id: ${e.result.resultId}, utteranceId: $utteranceId")
                e.close()

//                if (engineState == Lifecycle.State.STOPPED && isConnected) {
//                    disconnect()
//                } else if (engineState == Lifecycle.State.DESTROYED) {
//                    destroySynthesizer()
//                }

                // 这儿只是下载完了音频，但还没念完所有的语音，所以不结束
            }

            SynthesisCanceled.addEventListener { _: Any?, e: SpeechSynthesisEventArgs ->
                val cancellationDetails = try {
                    SpeechSynthesisCancellationDetails.fromResult(e.result).toString()
                } catch (e: Exception) {
                    L.d("$tag synthesis canceled failed error = ${e.message}")
                    ""
                }

                val utteranceId = resultIdMap.remove(e.result.resultId)
                utteranceId?.let { id ->
                    elapsedTimeout(id)
                }

                L.d("$tag tts synthesis canceled, result id: ${e.result.resultId}, utteranceId: $utteranceId")

                // FIXME 如果进入error状态，则无法恢复，暂时不上报

                e.close()
            }
        }

        connection = Connection.fromSpeechSynthesizer(synthesizer).apply {
            connected.addEventListener { _: Any?, e: ConnectionEventArgs? ->
                L.d("$tag tts connected")
                isConnected = true
                trigger.reset()
            }

            disconnected.addEventListener { _: Any?, _: ConnectionEventArgs? ->
                L.d("$tag tts disconnected")
                isConnected = false
                if (NetworkCompat.isConnected(context)) {
                    trigger.launch()
                }
            }
        }
    }

    private fun elapsedTimeout(id: String) {
        L.d("$tag elapsedTimeout id = $id")
        elapsedMap.remove(id)?.let {
//            MetricsCollector.observeTtsAzure(1000 * 10)
        }
    }

    private fun calculateElapsedTime(id: String) {
        L.d("$tag calculateElapsedTime id = $id")
        elapsedMap.remove(id)?.let {
//            MetricsCollector.observeTtsAzure(System.currentTimeMillis() - it)
        }
    }

    private fun connect() {
        L.d("$tag connect")
        try {
            //这里会报错
            connection?.openConnection(false)
        } catch (e: Exception) {
            L.d("$tag open connection failed = ${e.message}")
            trigger.continuation()
        }
    }

    private fun disconnect() {
        L.d("$tag disconnect")
        try {
            connection?.closeConnection()
        } catch (e: Exception) {
            L.d("$tag close connection failed = ${e.message}")
        }
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(Player.interruptNotificationAudioAttributes(), false)
            .setMediaSourceFactory(mediaSourceFactory)
            .setRenderersFactory(object : DefaultRenderersFactory(context) {

                override fun buildAudioSink(
                    context: Context,
                    enableFloatOutput: Boolean,
                    enableAudioTrackPlaybackParams: Boolean
                ): AudioSink? {
                    return DefaultAudioSink.Builder(context)
//                        .setAudioCapabilities(AudioCapabilities.getCapabilities(context))
                        .setAudioProcessors(arrayOf(volumeProcessor))
                        .setEnableFloatOutput(enableFloatOutput)
                        .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                        .build()
                }

            }).build()
    }

    private inner class AzureDataSource : BaseDataSource(true) {
        private var uri: Uri? = null
        private var audioDataStream: AudioDataStream? = null
        private var utteranceId: String? = null

        override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
            if (readLength == 0) {
                return 0
            }

            val newBuffer = ByteArray(readLength)
            val read = audioDataStream!!.readData(newBuffer).toInt()
//            L.i(LOG_TAG, "azure read data, offset: $offset, readLength: $readLength, read: $read")
            if (read == 0) {
                return C.RESULT_END_OF_INPUT
            }

            System.arraycopy(newBuffer, 0, buffer, offset, read)
            val sub = newBuffer.subBytes(0, read)
            byteCache = byteCache?.let {
                it + sub
            } ?: sub

            bytesTransferred(read)
            return read
        }

        override fun open(dataSpec: DataSpec): Long {
            transferInitializing(dataSpec)

            val leading = 300
            val name = dataSpec.uri.getQueryParameter("name") ?: "zh-CN-XiaoxiaoNeural"
            val rate = dataSpec.uri.getQueryParameter("rate") ?: "7%"
            val pitch = dataSpec.uri.getQueryParameter("pitch") ?: "0Hz"
            val volume = dataSpec.uri.getQueryParameter("volume") ?: "+1"
            val style = dataSpec.uri.getQueryParameter("style") ?: "chat"
            val text = dataSpec.uri.getQueryParameter("text")
            val content = SSML_TEMPLATE.format(name, leading, rate, pitch, volume, style, text)

            utteranceId = dataSpec.uri.getQueryParameter("utteranceId")

            synchronized(resultIdMap) {
                val result: SpeechSynthesisResult =
                    cachedTtsResults.remove(content)?.result
                        ?: synthesizer!!.StartSpeakingSsmlAsync(content).get()
                L.d("$tag tts start azure data source,resultId: ${result.resultId}, utteranceId: $utteranceId, content: $content")
                resultIdMap[result.resultId] = utteranceId!!
                elapsedMap[result.resultId] = System.currentTimeMillis()
                uri = dataSpec.uri
                audioDataStream = AudioDataStream.fromResult(result)
            }

            L.d("$tag tts player transfer started, stream: $audioDataStream")

            transferStarted(dataSpec)
            return C.LENGTH_UNSET.toLong()
        }

        override fun getUri(): Uri? {
            return uri
        }

        override fun close() {
            L.d("$tag tts player closing, stream: $audioDataStream")

            if (audioDataStream != null) {
                audioDataStream?.close()

                uri = null
                audioDataStream = null
                utteranceId = null

                transferEnded()
            }
        }
    }
}