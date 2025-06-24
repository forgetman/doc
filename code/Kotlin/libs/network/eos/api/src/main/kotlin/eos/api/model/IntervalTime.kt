package eos.api.model

import java.util.concurrent.TimeUnit

internal class IntervalTime(
    private val initialInterval: Long = 1,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS
) {
    companion object {
        private const val MAX_INTERVAL = 30L // 最大步长 30秒
    }

    private var currentInterval = initialInterval

    fun getInterval(): Long {
        return timeUnit.toMillis(currentInterval)
    }

    fun nextBackoff() {
        currentInterval = (currentInterval * 2).coerceAtMost(MAX_INTERVAL)
    }

    fun reset() {
        currentInterval = initialInterval
    }
}
