package feature.tts.def

import androidx.annotation.WorkerThread

fun interface SpeechOpCallback {

    @WorkerThread
    fun onResult(result: Boolean)
}