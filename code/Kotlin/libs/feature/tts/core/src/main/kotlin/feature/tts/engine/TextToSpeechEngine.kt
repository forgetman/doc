package feature.tts.engine

import androidx.annotation.WorkerThread
import feature.tts.def.SpeechOpCallback
import feature.tts.model.SpeechItem

interface TextToSpeechEngine {
    fun onInit(): Boolean
    fun onDeinit()

    fun onPreload(item: SpeechItem) {}

    @WorkerThread
    fun onStart(item: SpeechItem, callback: SpeechOpCallback)

    @WorkerThread
    fun onCancel(item: SpeechItem, callback: SpeechOpCallback)

    fun onDestroy()
}