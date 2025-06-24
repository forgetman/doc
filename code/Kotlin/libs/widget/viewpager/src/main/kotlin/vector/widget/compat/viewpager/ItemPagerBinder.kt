package vector.widget.compat.viewpager

import android.view.View
import android.view.ViewGroup
import vector.util.GenericUtil

abstract class ItemPagerBinder<T, VH : ItemPagerBinder.ViewHolder> {

    abstract fun createViewHolder(parent: ViewGroup): VH

    @Suppress("UNCHECKED_CAST")
    internal fun internalOnBindViewHolder(holder: ViewHolder, item: Any) {
        onBindViewHolder(holder as VH, item as T)
    }

    abstract fun onBindViewHolder(holder: VH, item: T)

    internal fun canBindData(item: Any): Boolean {
        val clz = GenericUtil.getClassType(this::class, 0)
        return clz.isInstance(item)
    }

    abstract class ViewHolder(val itemView: View)
}