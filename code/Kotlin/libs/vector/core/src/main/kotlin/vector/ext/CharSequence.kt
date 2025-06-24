package vector.ext

inline fun <C : CharSequence> C?.doOnNullOrEmpty(defaultValue: () -> C): C {
    return if (isNullOrEmpty()) defaultValue() else this
}

fun <C : CharSequence> C?.doOnNotNullOrEmpty(action: (C) -> Unit) {
    if (!isNullOrEmpty()) {
        action(this)
    }
}

fun <C : CharSequence> C?.doOnNotNullOrBlank(action: (C) -> Unit) {
    if (!isNullOrBlank()) {
        action(this)
    }
}