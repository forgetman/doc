package catroom.model

import catroom.bluetooth.model.RoomState

/**
 * RoomState wrapper
 */
data class RoomStateWrapper(val state: RoomState, val appVersion: Int, val systemVersion: String)