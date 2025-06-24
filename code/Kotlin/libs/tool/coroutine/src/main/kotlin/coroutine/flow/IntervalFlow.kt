package coroutine.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import java.util.concurrent.TimeUnit

fun intervalFlow(period: Long, unit: TimeUnit): Flow<Long> {
    return IntervalFlowImpl(period, unit)
}

fun intervalFlow(period: Long, initialDelay: Long, unit: TimeUnit): Flow<Long> {
    return IntervalFlowImpl(period, initialDelay, unit)
}

private class IntervalFlowImpl(
    private val period: Long,
    private val initialDelay: Long,
    private val unit: TimeUnit
) : Flow<Long> {

    constructor(
        period: Long,
        unit: TimeUnit
    ) : this(period, 0, unit)

    private var startNanos: Long = 0L
    private var lastNowNanos: Long = 0L

    private fun now(): Long = System.nanoTime()

    override suspend fun collect(collector: FlowCollector<Long>) {
        lastNowNanos = now()
        startNanos = lastNowNanos + unit.toNanos(initialDelay)

        val periodNanos = unit.toNanos(period)

        var count = 0
        var nextTick: Long
        while (true) {
            val nowNanos: Long = now()
            val interval: Long = ++count * periodNanos
            if (nowNanos < lastNowNanos || nowNanos >= lastNowNanos + periodNanos) {
                nextTick = nowNanos + periodNanos
                startNanos = nextTick - interval
            } else {
                nextTick = startNanos + interval
            }
            lastNowNanos = nowNanos

            val delayInNanos: Long = nextTick - nowNanos
            val delayInMillis: Long = TimeUnit.NANOSECONDS.toMillis(delayInNanos)
            delay(delayInMillis)

            val showTime: Long = unit.convert(interval, TimeUnit.NANOSECONDS)
            collector.emit(showTime)
        }
    }
}