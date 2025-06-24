package feature.tts.def

interface TtsPrefConst {
    object Name {
        const val DEBUGGABLE = "tts_debuggable"
        const val USE_ROBOTIC = "tts_use_robotic"
    }

    @Suppress("MayBeConstant")
    object Value {
        val DEBUGGABLE = false
        val USE_ROBOTIC = false
    }
}