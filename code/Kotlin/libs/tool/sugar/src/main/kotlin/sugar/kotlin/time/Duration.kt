package sugar.kotlin.time

import kotlin.time.Duration

/**
 * @Returns the number of minutes within an hour.
 */
val Duration.minutesInHour: Long
    get() = inWholeMinutes % 60

/**
 * @Returns the number of seconds within a minute.
 */
val Duration.secondsInMinute: Long
    get() = inWholeSeconds % 60