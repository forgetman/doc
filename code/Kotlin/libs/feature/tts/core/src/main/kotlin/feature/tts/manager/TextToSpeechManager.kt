package feature.tts.manager

import feature.tts.service.TextToSpeechService
import kotlinx.coroutines.flow.asStateFlow

object TextToSpeechManager {

    internal val service: TextToSpeechService = TextToSpeechService()
    val state = service.state.asStateFlow()

    /**
     * 取消所有speech
     */
    fun cancelAll() {
        service.cancelAll()
    }

    /**
     * 取消指定优先级的speech,
     */
    fun cancel(priority: Int) {
        service.cancel(priority)
    }

    fun pause() {
        service.pause()
    }

    fun resume() {
        service.resume()
    }

    /**
     * 取消比指定的priority低的speech
     */
    fun cancelLowerPriority(priority: Int) {
        service.cancelLowerPriority(priority)
    }
}