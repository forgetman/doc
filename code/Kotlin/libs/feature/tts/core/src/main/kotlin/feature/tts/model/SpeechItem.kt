package feature.tts.model

import feature.tts.stream.TextToSpeechInputStream

class SpeechItem internal constructor(
    val inputStream: TextToSpeechInputStream,
    val utteranceId: String,
    val params: Params
) {
    private var started = false
    private var canceled = false

    private var isRunning = false
        set(value) {
            synchronized(this) {
                field = value
            }
        }

    internal fun play(): Boolean {
        if (isRunning || started) {
            return false
        }
        started = true
        isRunning = true

        return true
    }

    internal fun cancel(): Boolean {
        if (canceled) {
            return false
        }
        isRunning = false
        canceled = true

        return true
    }

    fun isRunning(): Boolean {
        return isRunning
    }

    /**
     * 是否播放过
     */
    fun isStarted(): Boolean {
        return started
    }

    /**
     * 是否取消过
     */
    fun isCanceled(): Boolean {
        return canceled
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpeechItem) return false

        if (utteranceId != other.utteranceId) return false
        if (params != other.params) return false

        return true
    }

    override fun hashCode(): Int {
        var result = utteranceId.hashCode()
        result = 31 * result + params.hashCode()
        return result
    }

    override fun toString(): String {
        return "SpeechItem(utteranceId = $utteranceId)"
    }
}