package compat.telephony.def

/**
 * sim状态
 * @TelephonyManager.SimState的对齐
 */
enum class SimState {
    UNKNOWN, // 0
    ABSENT, // 1
    PIN_REQUIRED, // 2
    PUK_REQUIRED, // 3
    NETWORK_LOCKED, // 4
    READY, // 5
    NOT_READY, // 6
    PERM_DISABLED, // 7
    CARD_IO_ERROR, // 8
    CARD_RESTRICTED, // 9
    LOADED, // 10
    PRESENT; // 11

    companion object {
        fun acceptSystemState(state: Int): SimState {
            // 暂时使用罗列的方式, 不使用ordinal, 为了防止之后可能会添加新的不按顺序定义的状态
            return when (state) {
                0 -> UNKNOWN
                1 -> ABSENT
                2 -> PIN_REQUIRED
                3 -> PUK_REQUIRED
                4 -> NETWORK_LOCKED
                5 -> READY
                6 -> NOT_READY
                7 -> PERM_DISABLED
                8 -> CARD_IO_ERROR
                9 -> CARD_RESTRICTED
                10 -> LOADED
                11 -> PRESENT
                else -> UNKNOWN
            }
        }
    }

    fun isReady(): Boolean {
        return this == READY
    }
}