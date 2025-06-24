package feature.tts.model

import sugar.ext.self

class Params private constructor(
    val volume: Float,
    val volumeGain: Int,
    val speed: Float,
    val pitch: Int,
    val style: String,
    val speaker: String?
) {

    companion object {
        val DEFAULT = Builder().build()
    }

    class Builder internal constructor(params: Params?) {
        constructor() : this(null)

        private var volume: Float = 1f
        private var volumeGain: Int = 0
        private var speed = 0.9f
        private var pitch = 0
        private var style = "chat"
        private var speaker: String? = null

        init {
            params?.let {
                volume = it.volume
                volumeGain = it.volumeGain
                speed = it.speed
                pitch = it.pitch
                style = it.style
                speaker = it.speaker
            }
        }

        fun volume(volume: Float) = self {
            this.volume = volume
        }

        fun volumeGain(volumeGain: Int) = self {
            this.volumeGain = volumeGain
        }

        fun speed(speed: Float) = self {
            this.speed = speed
        }

        fun pitch(pitch: Int) = self {
            this.pitch = pitch
        }

        fun style(style: String) = self {
            this.style = style
        }

        fun speaker(speaker: String?) = self {
            this.speaker = speaker
        }

        fun build(): Params {
            return Params(volume, volumeGain, speed, pitch, style, speaker)
        }
    }

    fun buildUpon(): Builder {
        return Builder(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Params) return false

        if (volume != other.volume) return false
        if (volumeGain != other.volumeGain) return false
        if (speed != other.speed) return false
        if (pitch != other.pitch) return false
        if (style != other.style) return false
        if (speaker != other.speaker) return false

        return true
    }

    override fun hashCode(): Int {
        var result = volume.hashCode()
        result = 31 * result + volumeGain
        result = 31 * result + speed.hashCode()
        result = 31 * result + pitch
        result = 31 * result + style.hashCode()
        result = 31 * result + speaker.hashCode()
        return result
    }
}