package sugar.ext

fun <E> Comparable<E>.isAtLeast(state: E): Boolean {
    return compareTo(state) >= 0
}