package live.ext

import androidx.lifecycle.LiveData

fun LiveData<Long>.requireValue() = value ?: 0L

operator fun LiveData<Long>.plus(other: Int) = requireValue().plus(other)
operator fun LiveData<Long>.plus(other: Long) = requireValue().plus(other)
operator fun LiveData<Long>.plus(other: Float) = requireValue().plus(other)
operator fun LiveData<Long>.plus(other: Double) = requireValue().plus(other)

operator fun LiveData<Long>.minus(other: Int) = requireValue().minus(other)
operator fun LiveData<Long>.minus(other: Long) = requireValue().minus(other)
operator fun LiveData<Long>.minus(other: Float) = requireValue().minus(other)
operator fun LiveData<Long>.minus(other: Double) = requireValue().minus(other)

operator fun LiveData<Long>.times(other: Int) = requireValue().times(other)
operator fun LiveData<Long>.times(other: Long) = requireValue().times(other)
operator fun LiveData<Long>.times(other: Float) = requireValue().times(other)
operator fun LiveData<Long>.times(other: Double) = requireValue().times(other)

operator fun LiveData<Long>.div(other: Int) = requireValue().div(other)
operator fun LiveData<Long>.div(other: Long) = requireValue().div(other)
operator fun LiveData<Long>.div(other: Float) = requireValue().div(other)
operator fun LiveData<Long>.div(other: Double) = requireValue().div(other)

operator fun LiveData<Long>.compareTo(other: Int): Int {
    return value?.compareTo(other) ?: -1
}
