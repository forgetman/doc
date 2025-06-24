package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import live.refresh


val <E, T : ArrayList<E>> LiveData<T>.lastIndex: Int
    get() = value?.lastIndex ?: 0

val <E, T : ArrayList<E>> LiveData<T>.size: Int
    get() = value?.size ?: 0

fun <E, T : ArrayList<E>> MutableLiveData<T>.clear() {
    value?.clear()
    refresh()
}

fun <E, T : ArrayList<E>> MutableLiveData<T>.add(element: E) {
    value?.add(element)
    refresh()
}

fun <E, T : ArrayList<E>> MutableLiveData<T>.addAll(other: List<E>?) {
    if (other == null) return
    value?.addAll(other)
    refresh()
}

fun <E, T : ArrayList<E>> MutableLiveData<T>.removeAll(other: List<E>?) {
    if (other == null) return
    value?.removeAll(other)
    refresh()
}

fun <E, T : ArrayList<E>> MutableLiveData<T>.removeLast(): Boolean {
    if (value?.size == 0) return false
    value?.removeAt(lastIndex)
    refresh()
    return true
}

operator fun <E, T : ArrayList<E>> MutableLiveData<T>.plusAssign(element: E) {
    add(element)
}

operator fun <E, T : ArrayList<E>> MutableLiveData<T>.plusAssign(other: List<E>?) {
    addAll(other)
}

operator fun <E, T : ArrayList<E>> MutableLiveData<T>.minusAssign(other: List<E>?) {
    removeAll(other)
}

operator fun <E, T : ArrayList<E>> MutableLiveData<T>.get(index: Int) = value?.getOrNull(index)
