package coroutine.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

fun timerFlow(delay: Long, unit: TimeUnit) = flow {
    val delayMillis = unit.toMillis(delay)
    delay(delayMillis)
    emit(delay)
}