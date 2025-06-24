package feature.tts.model.op

import feature.tts.def.SpeechOpCallback
import feature.tts.model.SpeechItemWrapper

internal class SpeechPlayOp(wrapper: SpeechItemWrapper) : SpeechOp(wrapper) {

    override fun run(callback: SpeechOpCallback) {
        wrapper.engine?.start(wrapper.base, callback) ?: callback.onResult(false)
    }

    override fun toString(): String {
        return "PlayOp(item = $wrapper)"
    }
}