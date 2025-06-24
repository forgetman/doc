package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow


operator fun MutableStateFlow<Float>.minus(other: Int) = value.minus(other)
operator fun MutableStateFlow<Float>.minus(other: Float) = value.minus(other)

operator fun MutableStateFlow<Float>.plus(other: Int) = value.plus(other)
operator fun MutableStateFlow<Float>.plus(other: Float) = value.plus(other)

operator fun MutableStateFlow<Float>.minusAssign(other: Int) {
    value = minus(other)
}

operator fun MutableStateFlow<Float>.minusAssign(other: Float) {
    value = minus(other)
}

operator fun MutableStateFlow<Float>.plusAssign(other: Int) {
    value = plus(other)
}

operator fun MutableStateFlow<Float>.plusAssign(other: Float) {
    value = plus(other)
}