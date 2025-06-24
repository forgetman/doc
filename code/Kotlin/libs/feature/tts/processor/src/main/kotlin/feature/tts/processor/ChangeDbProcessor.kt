package feature.tts.processor

import feature.tts.def.Constants
import logger.L
import java.nio.ShortBuffer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

internal class ChangeDbProcessor(private val gain: Int) {
    companion object {
        private const val LOG_TAG = "ChangeDbProcessor"

        private const val FRAME_LENGTH = 2048
        private const val WINDOW_LENGTH = FRAME_LENGTH / 2
    }

    private var start = true

    private var input = FloatArray(FRAME_LENGTH)
    private var inputSize = 0
    private var totalInput = 0

    private var output = FloatArray(FRAME_LENGTH)
    private var outputSize = 0
    private var totalOutput = 0

    private fun ensureSize(buffer: ShortBuffer, data: FloatArray, length: Int): FloatArray {
        if (length + buffer.remaining() < data.size) {
            return data
        }

        return data.copyOf((ceil((length + buffer.remaining()) / FRAME_LENGTH.toFloat()) * FRAME_LENGTH).toInt())
    }

    fun queueInput(buffer: ShortBuffer) {
        input = ensureSize(buffer, input, inputSize)
        output = ensureSize(buffer, output, outputSize)

        L.groupBy(
            "$LOG_TAG, queueInput",
            "position = ${buffer.position()}",
            "remaining = ${buffer.remaining()}",
            "inputSize = $inputSize",
            "outputSize = $outputSize"
        ).d(Constants.TTS_LOG_TAG)

        for (i in buffer.position() until buffer.position() + buffer.remaining()) {
            input[inputSize++] = buffer.get(i) / 32768f
        }

        totalInput += buffer.remaining()
        process()
    }

    fun queueEndOfStream() {
        while (inputSize < FRAME_LENGTH) {
            input[inputSize++] = 0f
        }

        process()

        L.d(Constants.TTS_LOG_TAG, "$LOG_TAG queueEndOfStream, totalInput = $totalInput, totalOutput = $totalOutput")
    }

    private fun calcScale(data: FloatArray, offset: Int, length: Int): Float {
        val max = data.asList().subList(offset, offset + length).maxOf { abs(it) }
        return min(10.0.pow(gain / 20.0), 1.0 / (max + 0.01)).toFloat()
    }

    private fun calcDb(data: FloatArray, offset: Int, length: Int): Double {
        val rms = data.asList().subList(offset, offset + length).fold(0f) { acc, d -> acc + d * d }
        return 20 * log10(sqrt(rms / length) + 1e-12)
    }

    private fun process() {
        var current = 0
        while (current + FRAME_LENGTH <= inputSize) {
            val db = calcDb(input, current, FRAME_LENGTH)
            val scale = if (db >= -10 || db <= -35) {
                // 原本帧音量太小或太大， 不做处理
                1f
            } else {
                calcScale(input, current, FRAME_LENGTH)
            }

            if (start) {
                for (i in 0 until FRAME_LENGTH) {
                    output[outputSize + i] = input[current + i] * scale
                }
                outputSize += FRAME_LENGTH
            } else {
                for (i in 0 until WINDOW_LENGTH) {
                    output[outputSize + i] = input[current + WINDOW_LENGTH + i] * scale
                }
                outputSize += WINDOW_LENGTH
            }

            start = false
            current += WINDOW_LENGTH
        }

        L.d(
            Constants.TTS_LOG_TAG,
            "$LOG_TAG process, inputSize = $inputSize, processed = $current, outputSize = $outputSize"
        )
        System.arraycopy(input, current, input, 0, inputSize - current)
        inputSize -= current
    }

    fun getOutputSize(): Int {
        return outputSize * 2
    }

    fun getOutput(buffer: ShortBuffer) {
        val size = min(buffer.remaining(), outputSize)
        L.d(Constants.TTS_LOG_TAG, "$LOG_TAG getOutput, size = $size, outputSize = $outputSize")

        totalOutput += size

        for (i in 0 until size) {
            buffer.put((output[i] * 32767f).toInt().toShort())
        }

        if (size == outputSize) {
            outputSize = 0
        } else {
            System.arraycopy(output, size, output, 0, outputSize - size)
            outputSize -= size
        }
    }

    fun flush() {
        L.groupBy(
            "$LOG_TAG, flush",
            "input= $totalInput",
            "output = $totalOutput"
        ).d(Constants.TTS_LOG_TAG)

        outputSize = 0
        inputSize = 0
        start = true
        totalInput = 0
        totalOutput = 0
    }
}
