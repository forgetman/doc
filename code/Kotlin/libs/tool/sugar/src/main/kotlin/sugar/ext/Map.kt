package sugar.ext

import kotlin.collections.forEach

fun <K, V> MutableMap<K, V>.remove(value: V): K? {
    forEach {
        if (it.value == value) {
            val removed = remove(it.key)
            if (removed != null) {
                return it.key
            }
        }
    }
    return null
}