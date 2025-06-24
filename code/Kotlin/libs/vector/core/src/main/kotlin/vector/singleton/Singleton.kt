@file:Suppress("UNCHECKED_CAST")

package vector.singleton

interface Singleton<R, T> {
    fun getInstance(param: R): T

    companion object {
        fun <R, T> create(creator: (R) -> T): Singleton<R, T> = SingletonImpl(creator)
    }
}

fun <R, T> Singleton(creator: (R) -> T) = Singleton.create(creator)

private class SingletonImpl<R, T>(private val creator: (R) -> T) : Singleton<R, T> {
    @Volatile
    private var instance: T? = null

    override fun getInstance(param: R): T {
        return instance ?: synchronized(this) {
            creator(param).also { instance = it }
        }
    }
}