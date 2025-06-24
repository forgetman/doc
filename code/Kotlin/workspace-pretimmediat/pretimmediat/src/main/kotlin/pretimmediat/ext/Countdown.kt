package pretimmediat.ext

import coroutine.flow.intervalFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import logger.L
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "countdown"

/**
 * 倒计时
 */
fun countdownFlow(maxCount: Int, interval: Long, unit: TimeUnit): Flow<Int> {
    var currCount = 0
    return intervalFlow(interval, unit).map {
        currCount++
        val second = maxCount - currCount
        if (second <= 0) {
            0
            throw IllegalStateException("倒计时结束")
        } else {
            second
        }
    }.catch { e ->
        L.e(LOG_TAG, "countDownFlow", e)
    }.onStart {
        L.d(LOG_TAG, "startCountDown")
        emit(maxCount)
    }.onCompletion {
        emit(0)
    }.flowOn(Dispatchers.IO)
}