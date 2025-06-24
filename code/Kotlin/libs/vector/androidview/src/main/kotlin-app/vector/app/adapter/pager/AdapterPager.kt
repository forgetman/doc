@file:Suppress("unused")

package vector.app.adapter.pager

import sugar.ext.self

abstract class AdapterPager<T> {

    fun interface PagerCreator<T> {
        fun createInstance(position: Int): T?
    }

    var size: Int = 0
        private set
        get() {
            return creators?.size ?: field
        }

    var titles: List<String>? = null
    var creators: List<PagerCreator<T>>? = null
    var requiredCurrentItem: Int? = null

    fun createInstance(position: Int): T? {
        val constructor = creators?.getOrNull(position)
        return constructor?.createInstance(position)
    }

    abstract class Builder<T, R : AdapterPager<T>> {
        private var size: Int = 0

        private var creators: MutableList<PagerCreator<T>>? = null

        private var titles: List<String>? = null
        private var requiredCurrentItem: Int? = null

        fun size(value: Int) = self {
            size = value
        }

        fun creators(value: List<PagerCreator<T>>?) = self {
            this.creators = value?.toMutableList()
        }

        fun creators(vararg value: PagerCreator<T>) = self {
            if (this.creators == null) this.creators = mutableListOf()
            this.creators?.addAll(value)
        }

        fun titles(value: List<String>?) = self {
            titles = value
        }

        fun requiredCurrentItem(currentItem: Int?) = self {
            requiredCurrentItem = currentItem
        }

        protected abstract fun createPager(): R

        fun build(): R {
            val pager = createPager()

            pager.size = size
            pager.creators = creators
            pager.titles = titles
            pager.requiredCurrentItem = requiredCurrentItem

            return pager
        }
    }
}