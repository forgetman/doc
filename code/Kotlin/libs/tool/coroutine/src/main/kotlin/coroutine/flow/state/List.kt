package coroutine.flow.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val <E, T : List<E>> StateFlow<T>.lastIndex: Int
    get() = value.lastIndex

val <E, T : List<E>?> StateFlow<T>.size: Int
    get() = value?.size ?: 0

val <E, T : List<E>?> StateFlow<T>.isEmpty: Boolean
    get() = size == 0

val <E, T : List<E>?> StateFlow<T>.isNotEmpty: Boolean
    get() = size != 0

fun <E, T : List<E>?> StateFlow<T>.forEach(action: (E) -> Unit) {
    value?.forEach(action)
}

fun <E, T : List<E>?> StateFlow<T>.requireValue() = value ?: emptyList()

fun <E, T : List<E>?> StateFlow<T>.forEachIndexed(action: (index: Int, E) -> Unit) {
    value?.forEachIndexed(action)
}

operator fun <E, T : List<E>> StateFlow<T>.get(index: Int) = value.getOrNull(index)

@JvmName("getNullable")
operator fun <E, T : List<E>> StateFlow<T?>.get(index: Int) =
    value?.getOrNull(index)

fun <E, T : List<E>> MutableStateFlow<T>.refresh() {
    value = value
}

fun <E, T : List<E>> MutableStateFlow<T>.clear() {
    when (val list = value) {
        is MutableList<*> -> {
            list.clear()
            refresh()
        }
        else -> {
            @Suppress("UNCHECKED_CAST")
            value = emptyList<E>() as T
        }
    }
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.add(element: E) {
    value.add(element)
    refresh()
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.add(index: Int, element: E) {
    value.add(index, element)
    refresh()
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.addAll(other: List<E>?) {
    if (other == null) return
    value.addAll(other)
    refresh()
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.removeAll(other: List<E>?) {
    if (other == null) return
    value.removeAll(other)
    refresh()
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.removeLast(): Boolean {
    if (value.size == 0) return false
    val item = value.removeAt(lastIndex)
    val result = item != null
    if (result) refresh()
    return result
}

fun <E, T : MutableList<E>> MutableStateFlow<T>.removeFirst(): Boolean {
    if (value.size == 0) return false
    val item = value.removeAt(0)
    val result = item != null
    if (result) refresh()
    return result
}

@JvmName("removeNonNull")
fun <E, T : MutableList<E>> MutableStateFlow<T>.remove(item: E): Boolean {
    val result = value.remove(item)
    if (result) refresh()
    return result
}

fun <E, T : MutableList<E>> MutableStateFlow<T?>.remove(item: E): Boolean {
    val result = value?.remove(item) ?: false
    if (result) {
        value = value
    }
    return result
}

operator fun <E, T : MutableList<E>> MutableStateFlow<T>.plusAssign(element: E) {
    add(element)
}

operator fun <E, T : MutableList<E>> MutableStateFlow<T>.plusAssign(other: List<E>?) {
    addAll(other)
}

operator fun <E, T : MutableList<E>> MutableStateFlow<T>.minusAssign(other: List<E>?) {
    removeAll(other)
}