package feature.tts.ext

import feature.tts.stream.TextToSpeechInputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun TextToSpeechInputStream.readAll(): String {
    return buildString {
        do {
            val char = read()
            if (char != null) {
                append(char)
            }
        } while (char != null)

        reset()
    }
}

fun TextToSpeechInputStream.charFlow(): Flow<Char> {
    return callbackFlow {
        do {
            val char = read()
            if (char != null) {
                send(char)
            } else {
                close()
            }
        } while (char != null)

        reset()
    }
}