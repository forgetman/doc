package feature.tts.processor

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import feature.tts.def.Constants
import logger.L
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(UnstableApi::class)
class VolumeProcessor : AudioProcessor {

    companion object {
        private const val LOG_TAG = "VolumeProcessor"
    }

    var gain = 0
        set(value) {
            if (field != value) {
                L.d(Constants.TTS_LOG_TAG, "$LOG_TAG new gain = $value")
                field = value
                pendingSonicRecreation = true
            }
        }

    private var pendingInputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var pendingOutputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET

    private var pendingSonicRecreation = false
    private var sonic: ChangeDbProcessor? = null
    private var buffer = AudioProcessor.EMPTY_BUFFER
    private var shortBuffer = buffer.asShortBuffer()
    private var outputBuffer = AudioProcessor.EMPTY_BUFFER
    private var inputBytes: Long = 0
    private var outputBytes: Long = 0
    private var inputEnded = false

    @Throws(AudioProcessor.UnhandledAudioFormatException::class)
    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        L.d(Constants.TTS_LOG_TAG, "$LOG_TAG configure, gain = $gain, inputAudioFormat = $inputAudioFormat")

        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }

        if (inputAudioFormat.channelCount != 1) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }

        pendingInputAudioFormat = inputAudioFormat
        pendingOutputAudioFormat = AudioProcessor.AudioFormat(
            inputAudioFormat.sampleRate,
            inputAudioFormat.channelCount,
            C.ENCODING_PCM_16BIT
        )
        pendingSonicRecreation = true
        return pendingOutputAudioFormat
    }

    override fun isActive(): Boolean {
        return pendingOutputAudioFormat.sampleRate != Format.NO_VALUE && gain != 0
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) {
            return
        }
        val sonic = Assertions.checkNotNull(sonic)
        val shortBuffer = inputBuffer.asShortBuffer()
        val inputSize = inputBuffer.remaining()
        inputBytes += inputSize.toLong()
        sonic.queueInput(shortBuffer)
        inputBuffer.position(inputBuffer.position() + inputSize)
    }

    override fun queueEndOfStream() {
        sonic?.queueEndOfStream()
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val sonic = sonic
        if (sonic != null) {
            val outputSize = sonic.getOutputSize()
            if (outputSize > 0) {
                if (buffer.capacity() < outputSize) {
                    buffer = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder())
                    shortBuffer = buffer.asShortBuffer()
                } else {
                    buffer.clear()
                    shortBuffer.clear()
                }
                sonic.getOutput(shortBuffer)
                outputBytes += outputSize.toLong()
                buffer.limit(outputSize)
                outputBuffer = buffer
            }
        }
        val outputBuffer = outputBuffer
        this.outputBuffer = AudioProcessor.EMPTY_BUFFER
        return outputBuffer
    }

    override fun isEnded(): Boolean {
        return inputEnded && (sonic == null || sonic!!.getOutputSize() == 0)
    }

    override fun flush() {
        if (isActive) {
            inputAudioFormat = pendingInputAudioFormat
            outputAudioFormat = pendingOutputAudioFormat
            if (pendingSonicRecreation) {
                sonic = ChangeDbProcessor(
                    gain
                )
            } else if (sonic != null) {
                sonic!!.flush()
            }
        }
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        inputBytes = 0
        outputBytes = 0
        inputEnded = false
    }

    override fun reset() {
        gain = 1
        pendingInputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
        pendingOutputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
        inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
        outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
        buffer = AudioProcessor.EMPTY_BUFFER
        shortBuffer = buffer.asShortBuffer()
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        pendingSonicRecreation = false
        sonic = null
        inputBytes = 0
        outputBytes = 0
        inputEnded = false
    }
}