package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import live.Live


fun LiveData<Boolean>.requireValue() = value ?: false

fun LiveData<Boolean>.isTrue() = requireValue()
fun LiveData<Boolean>.isFalse() = !requireValue()

fun MutableLiveData<Boolean>.toTrue() {
    value = true
}

fun MutableLiveData<Boolean>.toFalse() {
    value = false
}

/**
 * 反转
 */
fun MutableLiveData<Boolean>.inverse() {
    value = not()
}

operator fun LiveData<Boolean>.not() = value?.not() ?: false

operator fun Live<Boolean>.compareTo(other: Boolean) = value?.compareTo(other) ?: -1