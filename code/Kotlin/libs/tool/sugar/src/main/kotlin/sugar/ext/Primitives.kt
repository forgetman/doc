package sugar.ext

fun <T : Number, R> doOnNotZero(t: T?, block: (T) -> R?): R? {
    return if (t != null && t != 0) {
        block.invoke(t)
    } else {
        null
    }
}

fun <T1 : Number, T2 : Number, R> doOnNotZero(t1: T1?, t2: T2?, block: (t1: T1, t2: T2) -> R?): R? {
    return if (notNull(t1, t2)) {
        if (notZero(t1!!, t2!!)) {
            block.invoke(t1, t2)
        } else {
            null
        }
    } else null
}

fun <T1 : Number, T2 : Number, T3 : Number, R> doOnNotZero(
    t1: T1?,
    t2: T2?,
    t3: T3?,
    block: (t1: T1, t2: T2, t3: T3) -> R?
): R? {
    return if (notNull(t1, t2, t3)) {
        if (notZero(t1!!, t2!!, t3!!)) {
            block.invoke(t1, t2, t3)
        } else {
            null
        }
    } else null
}

private fun <T : Number> notZero(vararg ts: T): Boolean {
    ts.forEach {
        if (it == 0) return false
    }
    return true
}

/**
 * 判断不为空而且大于目标数
 */
inline fun <reified T : Number> T?.greaterThan(other: T, block: () -> Unit) {
    val i = this ?: return
    if (i > other) block()
}

inline fun <reified T : Number> T?.smallerThan(other: T, block: () -> Unit) {
    val i = this ?: return
    if (i < other) block()
}

inline fun <reified T : Number> T?.equals(other: T, block: () -> Unit) {
    val i = this ?: return
    if (i.compareTo(other) == 0) block()
}

operator fun <T : Number> T.compareTo(other: T): Int {
    return when (this) {
        is Int -> {
            when (other) {
                is Int -> compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        is Long -> {
            when (other) {
                is Int -> compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        is Float -> {
            when (other) {
                is Int -> compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        is Double -> {
            when (other) {
                is Int -> compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        is Short -> {
            when (other) {
                is Int -> compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        is Byte -> {
            when (other) {
                is Int -> this.compareTo(other)
                is Long -> compareTo(other)
                is Float -> compareTo(other)
                is Double -> compareTo(other)
                is Short -> compareTo(other)
                is Byte -> compareTo(other)
                else -> -1
            }
        }
        else -> -1
    }
}

fun <T : Number, R> T.map(action: (T) -> R): R {
    return action(this)
}

fun Int.forEach(action: (Int) -> Unit) {
    for (i in 0..this) {
        action(i)
    }
}

fun Long.forEach(action: (Long) -> Unit) {
    for (i in 0..this) {
        action(i)
    }
}
