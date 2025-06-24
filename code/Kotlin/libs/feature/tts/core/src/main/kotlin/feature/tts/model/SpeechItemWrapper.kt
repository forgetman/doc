package feature.tts.model

import feature.tts.engine.BaseTextToSpeechEngine
import java.lang.ref.WeakReference

class SpeechItemWrapper(
    val base: SpeechItem,
    val priority: Int,
    engine: BaseTextToSpeechEngine
) {
    private val weakRef = WeakReference(engine)
    val engine: BaseTextToSpeechEngine?
        get() = weakRef.get()

    override fun toString(): String {
        return "SpeechItemWrapper(base = $base, priority = $priority, engine = ${engine?.tag})"
    }
}