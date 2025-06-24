@file:Suppress("UNCHECKED_CAST")

package vector.singleton

interface Singleton2<T> {
    fun getInstance(): T

    companion object {
        fun <T> create(creator: () -> T): Singleton2<T> = Singleton2Impl(creator)
    }
}

fun <T> Singleton2(creator: () -> T) = Singleton2.create(creator)

private class Singleton2Impl<T>(private val creator: () -> T) : Singleton2<T> {

    @Volatile
    private var instance: T? = null

    override fun getInstance(): T {
        return instance ?: synchronized(this) {
            creator().also { instance = it }
        }
    }
}