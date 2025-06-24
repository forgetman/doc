package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun LiveData<Int>.requireValue() = value ?: 0

fun LiveData<Int?>.requiredValue() = value ?: 0

operator fun LiveData<Int>.plus(other: Int) = requireValue().plus(other)
operator fun LiveData<Int>.plus(other: Long) = requireValue().plus(other)
operator fun LiveData<Int>.plus(other: Float) = requireValue().plus(other)
operator fun LiveData<Int>.plus(other: Double) = requireValue().plus(other)

operator fun LiveData<Int>.minus(other: Int) = requireValue().minus(other)
operator fun LiveData<Int>.minus(other: Long) = requireValue().minus(other)
operator fun LiveData<Int>.minus(other: Float) = requireValue().minus(other)
operator fun LiveData<Int>.minus(other: Double) = requireValue().minus(other)

operator fun LiveData<Int>.times(other: Int) = requireValue().times(other)
operator fun LiveData<Int>.times(other: Long) = requireValue().times(other)
operator fun LiveData<Int>.times(other: Float) = requireValue().times(other)
operator fun LiveData<Int>.times(other: Double) = requireValue().times(other)

operator fun LiveData<Int>.div(other: Int) = requireValue().div(other)
operator fun LiveData<Int>.div(other: Long) = requireValue().div(other)
operator fun LiveData<Int>.div(other: Float) = requireValue().div(other)
operator fun LiveData<Int>.div(other: Double) = requireValue().div(other)

operator fun MutableLiveData<Int>.plusAssign(other: Int) {
    value = plus(other)
}

operator fun MutableLiveData<Int>.plusAssign(other: Float) {
    value = plus(other).toInt()
}

operator fun MutableLiveData<Int>.plusAssign(other: Double) {
    value = plus(other).toInt()
}

operator fun MutableLiveData<Int>.minusAssign(other: Int) {
    value = minus(other)
}

operator fun MutableLiveData<Int>.minusAssign(other: Float) {
    value = minus(other).toInt()
}

operator fun MutableLiveData<Int>.minusAssign(other: Double) {
    value = minus(other).toInt()
}

operator fun MutableLiveData<Int>.timesAssign(other: Int) {
    value = times(other)
}

operator fun MutableLiveData<Int>.timesAssign(other: Float) {
    value = times(other).toInt()
}

operator fun MutableLiveData<Int>.timesAssign(other: Double) {
    value = times(other).toInt()
}

operator fun MutableLiveData<Int>.divAssign(other: Int) {
    value = div(other)
}

operator fun MutableLiveData<Int>.divAssign(other: Float) {
    value = div(other).toInt()
}

operator fun MutableLiveData<Int>.divAssign(other: Double) {
    value = div(other).toInt()
}

operator fun LiveData<Int>.compareTo(other: Int): Int {
    return value?.compareTo(other) ?: -1
}
