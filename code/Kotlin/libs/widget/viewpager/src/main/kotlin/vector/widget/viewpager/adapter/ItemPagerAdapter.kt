package vector.widget.viewpager.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import sugar.ext.throwIfNull
import vector.app.adapter.Cache
import vector.app.adapter.SparseIntArrayCache
import vector.widget.compat.viewpager.ItemPagerBinder

class ItemPagerAdapter : PagerAdapter() {

    private val binders = mutableListOf<ItemPagerBinder<*, *>>()
    private val viewTypeCache: Cache = SparseIntArrayCache()

    var data: List<Any>? = null

    private var modify = false
    private var updateCount = 0

    val lastItemPosition: Int
        get() {
            val count = count
            return if (count == 0) 0 else count - 1
        }

    var itemCycle: Boolean = false

    val realCount: Int
        get() = data?.size ?: 0

    fun registerItemBinders(itemBinders: List<ItemPagerBinder<*, *>>) {
        binders.addAll(itemBinders)
    }

    private fun getItem(position: Int): Any? {
        var realPos = position
        if (itemCycle) {
            realPos %= realCount
        }
        return data?.get(realPos)
    }

    override fun getCount(): Int {
        val count = data?.size ?: 0
        return if (itemCycle) {
            if (count < 2) {
                count
            } else Integer.MAX_VALUE
        } else {
            count
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPos: Int = if (itemCycle) {
            position % realCount
        } else {
            position
        }

        val binder = binders[getItemViewType(realPos)]
        val holder = binder.createViewHolder(container)
        binder.internalOnBindViewHolder(holder, getItem(position).throwIfNull("Item not found"))

        container.addView(holder.itemView)

        return holder
    }

    private fun getItemViewType(position: Int): Int {
        var viewType = viewTypeCache[position, -1]
        if (viewType == -1) {
            val item = getItem(position) ?: return viewType
            viewType = getItemBinderPositionForItem(item)
            viewTypeCache.append(position, viewType)
        }
        return viewType
    }

    private fun getItemBinderPositionForItem(item: Any): Int {
        for ((binderPosition, itemBinder) in binders.withIndex()) {
            if (itemBinder.canBindData(item)) {
                return binderPosition
            }
        }
        throw IllegalStateException("ItemBinder not found for position. Item = $item")
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView((any as ItemPagerBinder.ViewHolder).itemView)
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        modify = true
        updateCount = 0
    }

    override fun getItemPosition(`object`: Any): Int {
        if (updateCount >= count) {
            modify = false
        }
        updateCount++

        return if (modify) {
            POSITION_NONE
        } else {
            POSITION_UNCHANGED
        }
    }
}
