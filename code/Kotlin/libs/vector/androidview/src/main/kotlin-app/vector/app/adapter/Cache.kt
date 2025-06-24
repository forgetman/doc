package vector.app.adapter

import android.util.SparseIntArray

interface Cache {
    fun append(key: Int, value: Int)
    operator fun get(key: Int, valueIfKeyNotFound: Int): Int
    fun clear()
}

class SparseIntArrayCache : Cache {
    private val cache = SparseIntArray()
    override fun append(key: Int, value: Int) {
        cache.append(key, value)
    }

    override fun get(key: Int, valueIfKeyNotFound: Int): Int {
        return cache[key, valueIfKeyNotFound]
    }

    override fun clear() {
        cache.clear()
    }
}