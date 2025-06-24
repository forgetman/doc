package sugar.collection

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import java.util.function.Consumer

@Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
class SafeLinkedList<T> : LinkedList<T>() {

    override fun add(element: T): Boolean {
        synchronized(this) {
            return super.add(element)
        }
    }

    override fun add(index: Int, element: T) {
        synchronized(this) {
            super.add(index, element)
        }
    }

    override fun addFirst(e: T) {
        synchronized(this) {
            super.addFirst(e)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getFirst(): T {
        synchronized(this) {
            return first()
        }
    }

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        if (isEmpty()) return null
        val array = LinkedList<T>()
        synchronized(this) {
            array.addAll(this)
        }
        array.forEach { element ->
            if (predicate(element)) return element
        }
        return null
    }

    override fun addAll(elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.addAll(elements)
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.addAll(index, elements)
        }
    }

    override fun clear() {
        synchronized(this) {
            super.clear()
        }
    }

    override fun remove(): T {
        synchronized(this) {
            return super.remove()
        }
    }

    override fun remove(element: T): Boolean {
        synchronized(this) {
            return super.remove(element)
        }
    }

    override fun removeAt(index: Int): T {
        synchronized(this) {
            return super.removeAt(index)
        }
    }

    override fun set(index: Int, element: T): T {
        synchronized(this) {
            return super.set(index, element)
        }
    }

    fun forEachElement(action: (T) -> Unit) {
        if (isEmpty()) return
        val array = LinkedList<T>()
        synchronized(this) {
            array.addAll(this)
        }
        array.forEach(action)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun forEach(action: Consumer<in T>) {
        synchronized(this) {
            super.forEach(action)
        }
    }
}

fun <T> safeLinkedListOf(): SafeLinkedList<T> = SafeLinkedList()