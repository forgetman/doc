package aop.util

class Interval {
    private var startTime: Long = 0
    private var endTime: Long = 0

    /**
     * 返回花费的时间
     */
    var elapsedTime: Long = 0
        private set

    private fun reset() {
        startTime = 0
        endTime = 0
        elapsedTime = 0
    }

    fun start() {
        reset()
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        if (startTime != 0L) {
            endTime = System.currentTimeMillis()
            elapsedTime = endTime - startTime
        } else {
            reset()
        }
    }
}