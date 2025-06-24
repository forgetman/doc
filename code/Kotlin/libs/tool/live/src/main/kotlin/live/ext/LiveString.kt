package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun LiveData<String>.requireValue() = value ?: ""

operator fun LiveData<String>.plus(other: Any?) = requireValue().plus(other)

operator fun MutableLiveData<String>.plusAssign(other: Any?) {
    value = plus(other)
}

fun MutableLiveData<String>.setValue(value: Float?) {
    setValue(value?.toString())
}

fun MutableLiveData<String>.setValue(value: Int?) {
    setValue(value?.toString())
}

fun LiveData<String>.isNullOrEmpty() = value.isNullOrEmpty()