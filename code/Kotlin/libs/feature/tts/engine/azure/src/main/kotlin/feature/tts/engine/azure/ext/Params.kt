package feature.tts.engine.azure.ext

import feature.tts.engine.azure.def.Constants
import feature.tts.model.Params

val Params.Companion.Azure_Xiaoxiao: Params
    get() = DEFAULT.buildUpon()
        .speaker(Constants.SPEAKER)
        .build()