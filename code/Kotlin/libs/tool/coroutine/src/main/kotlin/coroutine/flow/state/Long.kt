package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


operator fun MutableStateFlow<Long>.plus(other: Int) = value.plus(other)
operator fun MutableStateFlow<Long>.plus(other: Long) = value.plus(other)
operator fun MutableStateFlow<Long>.plus(other: Float) = value.plus(other)
operator fun MutableStateFlow<Long>.plus(other: Double) = value.plus(other)

operator fun MutableStateFlow<Long>.minus(other: Int) = value.minus(other)
operator fun MutableStateFlow<Long>.minus(other: Long) = value.minus(other)
operator fun MutableStateFlow<Long>.minus(other: Float) = value.minus(other)
operator fun MutableStateFlow<Long>.minus(other: Double) = value.minus(other)

operator fun MutableStateFlow<Long>.times(other: Int) = value.times(other)
operator fun MutableStateFlow<Long>.times(other: Long) = value.times(other)
operator fun MutableStateFlow<Long>.times(other: Float) = value.times(other)
operator fun MutableStateFlow<Long>.times(other: Double) = value.times(other)

operator fun MutableStateFlow<Long>.div(other: Int) = value.div(other)
operator fun MutableStateFlow<Long>.div(other: Long) = value.div(other)
operator fun MutableStateFlow<Long>.div(other: Float) = value.div(other)
operator fun MutableStateFlow<Long>.div(other: Double) = value.div(other)

operator fun StateFlow<Long>.compareTo(other: Int): Int {
    return value.compareTo(other)
}