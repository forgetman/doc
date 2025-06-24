package tool.trigger.strategy

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import kotlinx.coroutines.Job
import logger.L
import sugar.ext.runOnSubThread
import sugar.ext.self
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * @author yuansui
 * @since 2022/4/15
 * 重试机制, 支持时长叠加
 */
class BackoffStrategy private constructor(
    private val logTag: String,
    private val minBackoff: Long,
    private val maxBackoff: Long,
    private val multiple: Float,
) : Strategy() {

    companion object {
        private const val DEFAULT_MIN_BACKOFF = 1000L
        private const val DEFAULT_MAX_BACKOFF = 8000L
        private const val DEFAULT_MULTIPLE = 2f // 默认增长倍数
        private const val DEFAULT_LOG_TAG = "BackoffStrategy"
    }

    private var backoff = minBackoff
    private var job: Job? = null

    val currBackoff: Long
        get() = backoff


    @Suppress("unused")
    class Builder {
        private var logTag: String = DEFAULT_LOG_TAG
        private var minBackoff = DEFAULT_MIN_BACKOFF
        private var maxBackoff = DEFAULT_MAX_BACKOFF
        private var multiple: Float = DEFAULT_MULTIPLE

        fun tag(tag: String) = self {
            this.logTag = tag
        }

        fun minBackoff(@IntRange(from = 0) min: Long) = self {
            minBackoff = min
        }

        fun maxBackoff(@IntRange(from = 0) max: Long) = self {
            maxBackoff = max
        }

        fun multiple(@FloatRange(from = 0.1) m: Float) = self {
            multiple = m
        }

        fun build(): BackoffStrategy {
            return BackoffStrategy(logTag, minBackoff, maxBackoff, multiple)
        }
    }

    override fun onContinuation() {
        start()
    }

    override fun onReset() {
        job?.cancel()
        backoff = minBackoff
    }

    override fun onStart() {
        start()
    }

    override fun onStop() {
        job?.cancel()
        job = null
    }

    private fun start() {
        // 主动取消上一次可能未完成的任务
        job?.cancel()
        job = runOnSubThread(backoff, TimeUnit.MILLISECONDS) {
            achieved(true)
        }

        // 计算[backoff]下一次的最大值
        backoff = min((backoff * multiple).toLong(), maxBackoff)

        L.groupBy(
            "prepare retry, backoff = $backoff",
            "multiple = $multiple",
        ).d(logTag)
    }
}