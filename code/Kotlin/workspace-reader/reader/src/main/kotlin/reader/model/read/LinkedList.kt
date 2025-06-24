package reader.model.read

open class LinkedList<E> {

    var size: Int = 0

    var current: Node<E>? = null
        get() {
            if (field == null)
                field = first
            return field
        }
    var first: Node<E>? = null
    var last: Node<E>? = null

    fun addBefore(item: E) {
        val f = first
        val newNode = Node(item)
        first = newNode
        if (f == null) {
            last = newNode
        } else {
            f.prev = newNode
            newNode.next = f
        }
        size++
    }

    open fun addAllBefore(items: Collection<E>) {
        val newList = LinkedList<E>()

        items.forEach {
            newList.addLast(it)
        }

        val f = first
        first = newList.first
        if (f == null) {
            last = first
        } else {
            f.prev = newList.last
            newList.last?.next = f
        }
        size += newList.size
    }

    fun addLast(item: E) {
        val l = last
        val newNode = Node(item)
        last = newNode
        if (l == null) {
            first = newNode
        } else {
            l.next = newNode
            newNode.prev = l
        }
        size++
    }

    open fun addAllLast(items: Collection<E>) {
        val newList = LinkedList<E>()

        items.forEach {
            newList.addLast(it)
        }

        val l = last
        last = newList.last
        if (l == null) {
            first = newList.first
        } else {
            l.next = newList.first
            newList.first?.prev = l
        }
        size += newList.size
    }

    fun remove(item: E?): Boolean {
        if (item == null) {
            var x: Node<E>? = first
            while (x != null) {
                if (x.item == null) {
                    unlink(x)
                    return true
                }
                x = x.next
            }
        } else {
            var x: Node<E>? = first
            while (x != null) {
                if (item == x.item) {
                    unlink(x)
                    return true
                }
                x = x.next
            }
        }
        return false
    }

    private fun unlink(x: Node<E>) {
        val next = x.next
        val prev = x.prev

        if (prev == null) {
            first = next
        } else {
            prev.next = next
            x.prev = null
        }

        if (next == null) {
            last = prev
        } else {
            next.prev = prev
            x.next = null
        }

        x.item = null
        size--
    }

    fun prev(): E? {
        if (current == first) return null

        val prev = current?.prev
        if (prev != null) current = prev
        return prev?.item
    }

    fun next(): E? {
        if (current == last) return null

        val next = current?.next
        if (next != null) current = next
        return next?.item
    }

    open fun clear() {
        var x = first
        while (x != null) {
            val next = x.next
            x.item = null
            x.next = null
            x.prev = null
            x = next
        }
        last = null
        first = last
        size = 0
    }

    inner class Node<E>(item: E) {
        var prev: Node<E>? = null
        var next: Node<E>? = null
        var item: E? = null

        init {
            this.item = item
        }
    }
}