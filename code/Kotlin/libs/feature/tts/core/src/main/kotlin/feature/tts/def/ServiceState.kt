package feature.tts.def

import feature.tts.model.SpeechItemWrapper

sealed class ServiceState {
    data object IDLE : ServiceState()
    class PLAYING(val wrapper: SpeechItemWrapper) : ServiceState()
    data object CANCELING : ServiceState()
    data object PAUSED : ServiceState()
}