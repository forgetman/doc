package sugar.collection

import logger.L
import sugar.ext.remove
import sugar.ext.removeWhen

/**
 * 带有map功能的list
 *
 * @author yuansui
 */
@Suppress("unused")
class MapList<K, E> : List<E> {

    private val map = hashMapOf<K, E>()
    private val list = mutableListOf<E>()

    fun add(key: K, e: E): E? {
        var previous: E?
        synchronized(this) {
            previous = map.put(key, e)
            if (previous != null) {
                list.remove(previous)
            }
            list.add(e)
        }
        return previous
    }

    fun add(key: K, index: Int, e: E): E? {
        var previous: E?
        synchronized(this) {
            previous = map.put(key, e)
            if (previous != null) {
                list.remove(previous)
            }
            list.add(index, e)
        }
        return previous
    }

    fun addAll(vararg pairs: Pair<K, E>) {
        synchronized(this) {
            pairs.forEach { (k, v) ->
                map[k] = v
                list.add(v)
            }
        }
    }

    fun clear() {
        synchronized(this) {
            map.clear()
            list.clear()
        }
    }

    fun has(key: K): Boolean {
        return map[key] != null
    }

    fun remove(key: K): E? {
        var previous: E?
        synchronized(this) {
            previous = map.remove(key)
            if (previous != null) {
                list.remove(previous)
            }
        }
        return previous
    }

    fun remove(value: E): Boolean {
        var result: Boolean
        synchronized(this) {
            result = list.remove(value)
            if (result) {
                map.remove(value)
            }
        }
        return result
    }

    fun removeAt(index: Int): E? {
        if (index >= 0 && index <= list.lastIndex) {
            synchronized(this) {
                val element: E? = list.removeAt(index)
                if (element != null) {
                    map.remove(element)
                }
                return element
            }
        }
        return null
    }

    fun removeAll(elements: Collection<E>): Boolean {
        var ret = true
        synchronized(this) {
            elements.forEach {
                val result = list.remove(it)
                ret = ret.and(result)
                if (result) {
                    map.remove(it)
                }
            }
        }
        return ret
    }

    fun removeWhen(predicate: (E) -> Boolean) {
        synchronized(this) {
            val removed = list.removeWhen(predicate)
            L.www("removed: $removed")
            list.removeAll(removed)
            removed.forEach {
                map.remove(it)
            }
        }
    }

    fun <R : Comparable<R>> sortBy(selector: (E) -> R?) {
        list.sortBy(selector)
    }

    fun <R : Comparable<R>> sortByDescending(selector: (E) -> R?) {
        list.sortByDescending(selector)
    }

    fun get(key: K): E? {
        return map[key]
    }

    fun keys(): MutableSet<K> {
        return map.keys
    }

    override val size: Int
        get() = list.size

    override fun contains(element: E): Boolean {
        return list.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return list.containsAll(elements)
    }

    override fun get(index: Int): E {
        return list[index]
    }

    fun getOrNull(index: Int): E? {
        return list.getOrNull(index)
    }

    override fun indexOf(element: E): Int {
        return list.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun iterator(): Iterator<E> {
        return list.iterator()
    }

    override fun lastIndexOf(element: E): Int {
        return list.lastIndexOf(element)
    }

    override fun listIterator(): ListIterator<E> {
        return list.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<E> {
        return list.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        return list.subList(fromIndex, toIndex)
    }

    override fun toString(): String {
        return list.toString()
    }
}

fun <K, V> mapListOf(): MapList<K, V> = MapList()

fun <K, V> mapListOf(vararg pairs: Pair<K, V>): MapList<K, V> =
    MapList<K, V>().apply { addAll(*pairs) }
