package feature.tts.ext

import androidx.lifecycle.LifecycleOwner
import coroutine.scope.observeCancel
import feature.tts.TextToSpeech
import feature.tts.def.Error
import feature.tts.model.Params
import feature.tts.stream.CharSequenceInputStream
import kotlinx.coroutines.CoroutineScope
import sugar.ext.observeDestroy

fun TextToSpeech.speak(
    text: CharSequence,
    params: Params = Params.DEFAULT,
    utteranceId: String? = null,
    strategy: TextToSpeech.Strategy = TextToSpeech.Strategy.ADD
) {
    speak(CharSequenceInputStream(text), params, utteranceId, strategy)
}

fun TextToSpeech.onUtteranceStart(owner: LifecycleOwner, callback: (String?) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onStart(utteranceId: String) {
            callback(utteranceId)
        }
    }
    addUtteranceProgressListener(listener)
    owner.observeDestroy {
        removeUtteranceProgressListener(listener)
    }
}

fun TextToSpeech.onUtteranceEnd(owner: LifecycleOwner, callback: (String?) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onEnd(utteranceId: String) {
            callback(utteranceId)
        }
    }
    addUtteranceProgressListener(listener)
    owner.observeDestroy {
        removeUtteranceProgressListener(listener)
    }
}

fun TextToSpeech.onUtteranceError(owner: LifecycleOwner, callback: (String?, Error) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onError(utteranceId: String, error: Error) {
            callback(utteranceId, error)
        }
    }
    addUtteranceProgressListener(listener)
    owner.observeDestroy {
        removeUtteranceProgressListener(listener)
    }
}

fun TextToSpeech.onUtteranceStart(coroutineScope: CoroutineScope, callback: (String?) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onStart(utteranceId: String) {
            callback(utteranceId)
        }
    }
    addUtteranceProgressListener(listener)
    coroutineScope.observeCancel {
        removeUtteranceProgressListener(listener)
    }
}

fun TextToSpeech.onUtteranceEnd(coroutineScope: CoroutineScope, callback: (String?) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onEnd(utteranceId: String) {
            callback(utteranceId)
        }
    }
    addUtteranceProgressListener(listener)
    coroutineScope.observeCancel {
        removeUtteranceProgressListener(listener)
    }
}

fun TextToSpeech.onUtteranceError(coroutineScope: CoroutineScope, callback: (String?, Error) -> Unit) {
    val listener = object : TextToSpeech.UtteranceProgressListener {
        override fun onError(utteranceId: String, error: Error) {
            callback(utteranceId, error)
        }
    }
    addUtteranceProgressListener(listener)
    coroutineScope.observeCancel {
        removeUtteranceProgressListener(listener)
    }
}