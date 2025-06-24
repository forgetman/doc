package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


val <E, T : ArrayList<E>> StateFlow<T>.lastIndex: Int
    get() = value.lastIndex

val <E, T : ArrayList<E>> StateFlow<T>.size: Int
    get() = value.size

fun <E, T : ArrayList<E>> MutableStateFlow<T>.clear() {
    value.clear()
    refresh()
}

fun <E, T : ArrayList<E>> MutableStateFlow<T>.add(element: E) {
    value.add(element)
    refresh()
}

fun <E, T : ArrayList<E>> MutableStateFlow<T>.addAll(other: List<E>?) {
    if (other == null) return
    value.addAll(other)
    refresh()
}

fun <E, T : ArrayList<E>> MutableStateFlow<T>.removeAll(other: List<E>?) {
    if (other == null) return
    value.removeAll(other)
    refresh()
}

fun <E, T : ArrayList<E>> MutableStateFlow<T>.removeLast(): Boolean {
    if (value.isEmpty()) return false
    value.removeAt(lastIndex)
    refresh()
    return true
}

operator fun <E, T : ArrayList<E>> MutableStateFlow<T>.plusAssign(element: E) {
    add(element)
}

operator fun <E, T : ArrayList<E>> MutableStateFlow<T>.plusAssign(other: List<E>?) {
    addAll(other)
}

operator fun <E, T : ArrayList<E>> MutableStateFlow<T>.minusAssign(other: List<E>?) {
    removeAll(other)
}

operator fun <E, T : ArrayList<E>> StateFlow<T>.get(index: Int) = value.getOrNull(index)
