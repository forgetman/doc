@file:Suppress("unused")

package vector.widget.databinding.scrollable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import vector.os.Group
import vector.widget.scrollable.adapter.GroupAdapterEx
import vector.widget.scrollable.adapter.GroupMultiAdapterEx
import vector.widget.scrollable.adapter.GroupViewHolder

abstract class DBGroupMultiAdapterEx<GROUP : Group<CHILD>, CHILD>(private val owner: LifecycleOwner? = null) :
    GroupMultiAdapterEx<GROUP, CHILD, DBViewHolder>() {

    final override fun onCreateGroupViewHolder(
        groupPosition: Int,
        parent: ViewGroup?
    ): DBViewHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                getGroupLayoutId(getGroupType(groupPosition)),
                parent,
                false
            )
        binding.lifecycleOwner = owner
        return DBViewHolder(binding)
    }

    override fun onBindGroupViewHolder(groupViewType: Int, item: GROUP, holder: DBViewHolder) {
        val binding = holder.binding ?: return
        onBindGroupBinding(groupViewType, item, binding)
        binding.executePendingBindings()
    }

    override fun onCreateChildViewHolder(
        groupPosition: Int,
        childPosition: Int,
        parent: ViewGroup?
    ): DBViewHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                getChildLayoutId(getChildType(groupPosition, childPosition)),
                parent,
                false
            )
        binding.lifecycleOwner = owner
        return DBViewHolder(binding)
    }

    override fun onBindChildViewHolder(childViewType: Int, item: CHILD, holder: DBViewHolder) {
        val binding = holder.binding ?: return
        onBindChildBinding(childViewType, item, binding)
        binding.executePendingBindings()
    }

    abstract fun onBindGroupBinding(groupViewType: Int, item: GROUP, binding: ViewDataBinding)
    abstract fun onBindChildBinding(childViewType: Int, item: CHILD, binding: ViewDataBinding)
}

abstract class DBGroupAdapterEx<GROUP : Group<CHILD>, CHILD>(private val owner: LifecycleOwner? = null) :
    GroupAdapterEx<GROUP, CHILD, DBViewHolder>() {

    final override fun onCreateGroupViewHolder(
        groupPosition: Int,
        parent: ViewGroup?
    ): DBViewHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                getGroupLayoutId(),
                parent,
                false
            )
        binding.lifecycleOwner = owner
        return DBViewHolder(binding)
    }

    final override fun onBindGroupViewHolder(item: GROUP, holder: DBViewHolder) {
        val binding = holder.binding ?: return
        onBindGroupBinding(item, binding)
        binding.executePendingBindings()
    }

    abstract fun onBindGroupBinding(item: GROUP, binding: ViewDataBinding)

    final override fun onCreateChildViewHolder(
        groupPosition: Int,
        childPosition: Int,
        parent: ViewGroup?
    ): DBViewHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                getChildLayoutId(),
                parent,
                false
            )
        binding.lifecycleOwner = owner
        return DBViewHolder(binding)
    }

    override fun onBindChildViewHolder(item: CHILD, holder: DBViewHolder) {
        val binding = holder.binding ?: return
        onBindChildBinding(item, binding)
        binding.executePendingBindings()
    }

    abstract fun onBindChildBinding(item: CHILD, binding: ViewDataBinding)
}

class DBViewHolder(itemView: View) : GroupViewHolder(itemView) {
    var binding: ViewDataBinding? = null

    constructor(binding: ViewDataBinding) : this(binding.root) {
        this.binding = binding
    }
}