package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun MutableStateFlow<Boolean>.toTrue() {
    value = true
}

fun MutableStateFlow<Boolean>.toFalse() {
    value = false
}

fun StateFlow<Boolean>.isTrue() = value
fun StateFlow<Boolean>.isFalse() = !value

/**
 * 反转
 */
fun MutableStateFlow<Boolean>.inverse() {
    value = not()
}

operator fun MutableStateFlow<Boolean>.not() = value.not()

operator fun StateFlow<Boolean>.compareTo(other: Boolean) = value.compareTo(other)