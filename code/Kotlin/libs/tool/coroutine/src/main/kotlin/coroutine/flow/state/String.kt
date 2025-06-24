package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

operator fun MutableStateFlow<String>.plus(other: Any?) = value.plus(other)

operator fun MutableStateFlow<String>.plusAssign(other: Any?) {
    value = plus(other)
}

fun MutableStateFlow<String>.removeLast(): Boolean {
    if (value.isEmpty()) return false
    value = value.substring(0, value.lastIndex)
    return true
}

fun MutableStateFlow<String>.setValue(value: Float?) {
    this.value = value?.toString().orEmpty()
}

fun MutableStateFlow<String>.setValue(value: Int?) {
    this.value = value?.toString().orEmpty()
}

fun StateFlow<String>.isEmpty() = value.isEmpty()

fun StateFlow<String?>.isNullOrEmpty() = value.isNullOrEmpty()