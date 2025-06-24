package feature.tts.stream

interface TextToSpeechInputStream {
    fun read(): Char?
    fun reset()
}

class CharSequenceInputStream(private val text: CharSequence) : TextToSpeechInputStream {
    private var index = 0

    override fun read(): Char? {
        if (index >= text.length) {
            return null
        }
        return text[index++]
    }

    override fun reset() {
        index = 0
    }
}