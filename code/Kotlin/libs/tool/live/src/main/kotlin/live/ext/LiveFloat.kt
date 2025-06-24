package live.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


fun LiveData<Float>.requireValue() = value ?: 0f

operator fun LiveData<Float>.minus(other: Int) = requireValue().minus(other)
operator fun LiveData<Float>.minus(other: Float) = requireValue().minus(other)

operator fun LiveData<Float>.plus(other: Int) = requireValue().plus(other)
operator fun LiveData<Float>.plus(other: Float) = requireValue().plus(other)

operator fun MutableLiveData<Float>.minusAssign(other: Int) {
    value = minus(other)
}

operator fun MutableLiveData<Float>.minusAssign(other: Float) {
    value = minus(other)
}

operator fun MutableLiveData<Float>.plusAssign(other: Int) {
    value = plus(other)
}

operator fun MutableLiveData<Float>.plusAssign(other: Float) {
    value = plus(other)
}