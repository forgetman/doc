package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import live.refresh

val <E, T : List<E>> LiveData<out T>.lastIndex: Int
    get() = value?.lastIndex ?: 0

val <E, T : List<E>?> LiveData<out T>.size: Int
    get() = value?.size ?: 0

val <E, T : List<E>?> LiveData<out T>.isEmpty: Boolean
    get() = size == 0

val <E, T : List<E>?> LiveData<out T>.isNotEmpty: Boolean
    get() = size != 0

fun <E, T : List<E>?> LiveData<out T>.forEach(action: (E) -> Unit) {
    value?.forEach(action)
}

fun <E, T : List<E>?> LiveData<out T>.requireValue() = value ?: emptyList()

fun <E, T : List<E>?> LiveData<out T>.forEachIndexed(action: (index: Int, E) -> Unit) {
    value?.forEachIndexed(action)
}

operator fun <E, T : List<E>> LiveData<out T>.get(index: Int) = value?.getOrNull(index)

@JvmName("getNullable")
operator fun <E, T : List<E>> MutableLiveData<out T?>.get(index: Int) = value?.getOrNull(index)


fun <E, T : List<E>> MutableLiveData<T>.clear() {
    when (val list = value) {
        is MutableList<*> -> list.clear()
        is List<E> -> {
            @Suppress("UNCHECKED_CAST")
            value = emptyList<E>() as T
        }
    }
    refresh()
}

fun <E, T : MutableList<E>> MutableLiveData<T>.add(element: E) {
    value?.add(element)
    refresh()
}

fun <E, T : MutableList<E>> MutableLiveData<T>.add(index: Int, element: E) {
    value?.add(index, element)
    refresh()
}

fun <E, T : MutableList<E>> MutableLiveData<T>.addAll(other: List<E>?) {
    if (other == null) return
    value?.addAll(other)
    refresh()
}

fun <E, T : MutableList<E>> MutableLiveData<T>.removeAll(other: List<E>?) {
    if (other == null) return
    value?.removeAll(other)
    refresh()
}

fun <E, T : MutableList<E>> MutableLiveData<T>.removeLast(): Boolean {
    if (value?.size == 0) return false
    val item = value?.removeAt(lastIndex)
    val result = item != null
    if (result) refresh()
    return result
}

fun <E, T : MutableList<E>> MutableLiveData<T>.removeFirst(): Boolean {
    if (value?.size == 0) return false
    val item = value?.removeAt(0)
    val result = item != null
    if (result) refresh()
    return result
}

@JvmName("removeNonNull")
fun <E, T : MutableList<E>> MutableLiveData<T>.remove(item: E): Boolean {
    val result = value?.remove(item) ?: false
    if (result) refresh()
    return result
}

fun <E, T : MutableList<E>> MutableLiveData<T?>.remove(item: E): Boolean {
    val result = value?.remove(item) ?: false
    if (result) refresh()
    return result
}

operator fun <E, T : MutableList<E>> MutableLiveData<T>.plusAssign(element: E) {
    add(element)
}

operator fun <E, T : MutableList<E>> MutableLiveData<T>.plusAssign(other: List<E>?) {
    addAll(other)
}

operator fun <E, T : MutableList<E>> MutableLiveData<T>.minusAssign(other: List<E>?) {
    removeAll(other)
}