package bluetoothle.peripheral.server.internal

import java.util.*

internal data class Characteristic(
    val uuid: UUID,
    val properties: Int,
    val permissions: Int
)