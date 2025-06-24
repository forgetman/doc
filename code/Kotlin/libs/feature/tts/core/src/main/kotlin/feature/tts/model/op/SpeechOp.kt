package feature.tts.model.op

import feature.tts.def.SpeechOpCallback
import feature.tts.model.SpeechItemWrapper

internal abstract class SpeechOp(val wrapper: SpeechItemWrapper) {
    abstract fun run(callback: SpeechOpCallback)
}
