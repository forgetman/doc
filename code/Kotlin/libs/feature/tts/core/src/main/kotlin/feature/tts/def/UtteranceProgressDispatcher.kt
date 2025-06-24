package feature.tts.def

internal interface UtteranceProgressDispatcher {
    fun dispatchOnStart(utteranceId: String)
    fun dispatchOnEnd(utteranceId: String)
    fun dispatchOnError(utteranceId: String, error: Error)
}