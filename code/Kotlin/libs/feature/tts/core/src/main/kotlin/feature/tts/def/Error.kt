package feature.tts.def

sealed class Error(val code: Int, val message: String) {
    data object DuplicateUtteranceId : Error(0, "duplicate utterance id")
    data object Cancel : Error(1, "canceled")
    data object EngineNotInit : Error(2, "engine not init")
}