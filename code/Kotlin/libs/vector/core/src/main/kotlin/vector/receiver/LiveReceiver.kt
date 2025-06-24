package vector.receiver

object LiveReceiver {
    fun timeTick(unit: TimeTickUnit = TimeTickUnit.MINUTE, count: Long = 1) = TimeTickReceiver(
        unit,
        count
    )

    fun battery() = BatteryReceiver()

    fun screenOnOff() = ScreenOnOffReceiver()
}