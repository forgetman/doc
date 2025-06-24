package tool.trigger.strategy

import coroutine.flow.intervalFlow
import coroutine.flow.launchForever
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

/**
 * 周期触发器
 */
class PeriodStrategy(
    private val period: Long,
    private val timeUnit: TimeUnit,
    private val initialDelay: Long
) : Strategy() {

    private var job: Job? = null

    override fun onStart() {
        job = intervalFlow(period, initialDelay, timeUnit).onEach {
            achieved(false)
        }.launchForever()
    }

    override fun onStop() {
        job?.cancel()
        job = null
    }
}