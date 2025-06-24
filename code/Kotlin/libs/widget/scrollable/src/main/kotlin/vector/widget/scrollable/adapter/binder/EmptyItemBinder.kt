package vector.widget.scrollable.adapter.binder

import vector.widget.scrollable.adapter.ItemViewHolder

abstract class EmptyItemBinder<VH : ItemViewHolder> :
    ItemBinder<Unit, VH>() {

    final override fun onBindViewHolder(holder: VH, item: Unit, position: Int) {
        // do nothing
    }

    /**
     * 为了兼容语法问题
     */
    internal fun onBindViewHolder(
        holder: ItemViewHolder,
        @Suppress("UNUSED_PARAMETER") position: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        onBindViewHolder(holder as VH)
    }

    abstract fun onBindViewHolder(holder: VH)
}