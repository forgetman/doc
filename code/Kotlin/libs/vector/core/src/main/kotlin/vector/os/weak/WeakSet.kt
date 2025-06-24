package vector.os.weak

import java.util.WeakHashMap

/**
 * @author yuansui
 * @since 2019/4/17
 */
@Suppress("unused")
class WeakSet<E> : MutableSet<E> {

    companion object {
        // Dummy value to associate with an Object in the backing Map
        private val PRESENT = Any()
    }

    private val map = WeakHashMap<E, Any>()

    override fun add(element: E): Boolean {
        return map.put(element, PRESENT) == null
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var modified = false
        for (e in elements)
            if (add(e)) modified = true
        return modified
    }

    override fun clear() {
        map.clear()
    }

    override fun iterator(): MutableIterator<E> {
        return map.keys.iterator()
    }

    override fun remove(element: E): Boolean {
        return map.remove(element) === PRESENT
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var modified = false

        if (size > elements.size) {
            val i = elements.iterator()
            while (i.hasNext())
                modified = modified or remove(i.next())
        } else {
            val i = iterator()
            while (i.hasNext()) {
                if (elements.contains(i.next())) {
                    i.remove()
                    modified = true
                }
            }
        }
        return modified

    }

    override fun retainAll(elements: Collection<E>): Boolean {
        var modified = false
        val i = iterator()
        while (i.hasNext()) {
            if (!elements.contains(i.next())) {
                i.remove()
                modified = true
            }
        }
        return modified
    }

    override val size: Int
        get() = map.size

    override fun contains(element: E): Boolean {
        return map.containsKey(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        for (e in elements) {
            if (!contains(e)) return false
        }
        return true
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    fun get(element: E?) = map[element]
}