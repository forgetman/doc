package bluetoothle.peripheral.server.internal

import java.util.*

internal data class GattService(
    val uuid: UUID,
    val serviceType: Int,
    val characteristics: List<Characteristic>
)