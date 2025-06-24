@file:Suppress("unused")

package sugar.ext

fun <T : Any> MutableList<T?>.filterNotNull(): MutableList<T> {
    return filterNotNullTo(mutableListOf())
}

/**
 * 切割数组
 * @param size 每组的个数
 * @return 分组后的数组, 最后一组的个数可能小于[size]
 */
fun <E> List<E>.split(size: Int): List<List<E>> {
    val groups = mutableListOf<List<E>>()
    if (this.size <= size) {
        // 只有一组
        groups.add(this)
    } else {
        val remain = this.size % size
        val l = take(this.size - remain)

        var eachList: MutableList<E>? = null
        l.forEach {
            if (eachList == null) {
                eachList = mutableListOf<E>().apply {
                    groups.add(this)
                }
            }

            eachList.add(it)
            if (eachList.size == size) {
                // 需要一组新的
                eachList = null
            }
        }

        groups.add(takeLast(remain))
    }

    return groups
}

/**
 * 移除集合中满足给定条件的所有元素。
 * @param predicate 删除条件。true：删除
 */
fun <E> MutableList<E>.removeWhen(predicate: (E) -> Boolean): MutableList<E> {
    return filter(predicate).toMutableList()
}

/**
 * 移除集合中满足给定条件的第一个元素。
 */
fun <E> MutableList<E>.removeFirstWhen(predicate: (E) -> Boolean): MutableList<E> {
    val ret = mutableListOf<E>()
    ret.addAll(this)
    val item = firstOrNull(predicate)
    if (item != null) {
        ret.remove(item)
    }
    return ret
}

fun <E> MutableList<E>.move(from: Int, to: Int) {
    val item = getOrNull(from) ?: return
    when {
        from > to -> {
            // 先remove
            remove(item)
            add(to, item)
        }

        to > from -> {
            // 先add
            add(to, item)
            removeAt(from)
        }
    }
}

inline fun <reified E> Collection<E>.collectElements(): Array<E>? {
    var array: Array<E>? = null
    synchronized(this) {
        if (isNotEmpty()) {
            array = this.toTypedArray()
        }
    }
    return array
}