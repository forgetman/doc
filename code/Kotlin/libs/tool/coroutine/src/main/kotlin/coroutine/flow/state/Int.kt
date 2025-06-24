package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

operator fun MutableStateFlow<Int>.plus(other: Int) = value.plus(other)
operator fun MutableStateFlow<Int>.plus(other: Long) = value.plus(other)
operator fun MutableStateFlow<Int>.plus(other: Float) = value.plus(other)
operator fun MutableStateFlow<Int>.plus(other: Double) = value.plus(other)

operator fun MutableStateFlow<Int>.minus(other: Int) = value.minus(other)
operator fun MutableStateFlow<Int>.minus(other: Long) = value.minus(other)
operator fun MutableStateFlow<Int>.minus(other: Float) = value.minus(other)
operator fun MutableStateFlow<Int>.minus(other: Double) = value.minus(other)

operator fun MutableStateFlow<Int>.times(other: Int) = value.times(other)
operator fun MutableStateFlow<Int>.times(other: Long) = value.times(other)
operator fun MutableStateFlow<Int>.times(other: Float) = value.times(other)
operator fun MutableStateFlow<Int>.times(other: Double) = value.times(other)

operator fun MutableStateFlow<Int>.div(other: Int) = value.div(other)
operator fun MutableStateFlow<Int>.div(other: Long) = value.div(other)
operator fun MutableStateFlow<Int>.div(other: Float) = value.div(other)
operator fun MutableStateFlow<Int>.div(other: Double) = value.div(other)

operator fun MutableStateFlow<Int>.plusAssign(other: Int) {
    value = plus(other)
}

operator fun MutableStateFlow<Int>.plusAssign(other: Float) {
    value = plus(other).toInt()
}

operator fun MutableStateFlow<Int>.plusAssign(other: Double) {
    value = plus(other).toInt()
}

operator fun MutableStateFlow<Int>.minusAssign(other: Int) {
    value = minus(other)
}

operator fun MutableStateFlow<Int>.minusAssign(other: Float) {
    value = minus(other).toInt()
}

operator fun MutableStateFlow<Int>.minusAssign(other: Double) {
    value = minus(other).toInt()
}

operator fun MutableStateFlow<Int>.timesAssign(other: Int) {
    value = times(other)
}

operator fun MutableStateFlow<Int>.timesAssign(other: Float) {
    value = times(other).toInt()
}

operator fun MutableStateFlow<Int>.timesAssign(other: Double) {
    value = times(other).toInt()
}

operator fun MutableStateFlow<Int>.divAssign(other: Int) {
    value = div(other)
}

operator fun MutableStateFlow<Int>.divAssign(other: Float) {
    value = div(other).toInt()
}

operator fun MutableStateFlow<Int>.divAssign(other: Double) {
    value = div(other).toInt()
}

operator fun StateFlow<Int>.compareTo(other: Int): Int {
    return value.compareTo(other)
}
