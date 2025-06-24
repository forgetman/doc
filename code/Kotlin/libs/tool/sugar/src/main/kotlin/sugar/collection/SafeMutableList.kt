package sugar.collection

import android.os.Build
import androidx.annotation.RequiresApi
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import java.util.function.Consumer
import java.util.function.Predicate

@Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
class SafeMutableList<T>() : ArrayList<T>() {

    constructor(elements: Array<out T>) : this() {
        addAll(elements)
    }

    override fun add(element: T): Boolean {
        synchronized(this) {
            return super.add(element)
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.addAll(elements)
        }
    }

    override fun add(index: Int, element: T) {
        synchronized(this) {
            super.add(index, element)
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.addAll(index, elements)
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.removeAll(elements.toSet())
        }
    }

    override fun removeAt(index: Int): T {
        synchronized(this) {
            return super.removeAt(index)
        }
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        synchronized(this) {
            return if (isSdkAtLeast(SdkInt.N_24)) {
                super.removeIf(filter)
            } else {
                var removed = false
                val iterator = this.iterator()
                while (iterator.hasNext()) {
                    val element = iterator.next()
                    if (filter.test(element)) {
                        iterator.remove()
                        removed = true
                    }
                }
                return removed
            }
        }
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        synchronized(this) {
            super.removeRange(fromIndex, toIndex)
        }
    }

    override fun remove(element: T): Boolean {
        synchronized(this) {
            return super.remove(element)
        }
    }

    override fun clear() {
        synchronized(this) {
            super.clear()
        }
    }

    override fun contains(element: T): Boolean {
        synchronized(this) {
            return super.contains(element)
        }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        synchronized(this) {
            return super.containsAll(elements)
        }
    }

    override fun indexOf(element: T): Int {
        synchronized(this) {
            return super.indexOf(element)
        }
    }

    override fun lastIndexOf(element: T): Int {
        synchronized(this) {
            return super.lastIndexOf(element)
        }
    }

    /**
     * 和[forEach]同名会导致无法调用到本身的方法, 只能调用到拓展方法(kotlin的问题)
     */
    fun forEachElement(action: (T) -> Unit) {
        if (isEmpty()) return
        val array = mutableListOf<T>()
        synchronized(this) {
            array.addAll(this)
        }
        array.forEach(action)
    }

    inline fun forEachElementIndex(action: (Int, T) -> Unit) {
        if (isEmpty()) return
        val array = mutableListOf<T>()
        synchronized(this) {
            array.addAll(this)
        }
        array.forEachIndexed(action)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun forEach(action: Consumer<in T>) {
        synchronized(this) {
            super.forEach(action)
        }
    }
}

fun <T> safeMutableListOf(vararg elements: T): SafeMutableList<T> {
    return if (elements.isEmpty()) SafeMutableList(elements) else SafeMutableList(elements)
}

fun <T> safeMutableListOf(): SafeMutableList<T> = SafeMutableList()