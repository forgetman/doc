package vector.widget.scrollable.adapter.binder

import android.view.ViewGroup
import vector.util.GenericUtil
import vector.widget.scrollable.adapter.ItemViewHolder
import vector.widget.scrollable.delegate.SpanSizeDelegate

abstract class ItemBinder<T, VH : ItemViewHolder> : SpanSizeDelegate {

    abstract fun createViewHolder(parent: ViewGroup): VH

    @Suppress("UNCHECKED_CAST")
    internal fun internalOnBindViewHolder(holder: ItemViewHolder, item: Any, position: Int) {
        onBindViewHolder(holder as VH, item as T, position)
    }

    abstract fun onBindViewHolder(holder: VH, item: T, position: Int)

    internal fun canBindData(item: Any): Boolean {
        val clz = GenericUtil.getClassType(this::class, 0)
        return clz.isInstance(item)
    }
}