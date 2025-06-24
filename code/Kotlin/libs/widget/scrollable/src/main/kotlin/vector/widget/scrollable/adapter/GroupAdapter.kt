package vector.widget.scrollable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import sugar.ext.throwIfNull
import sugar.util.ReflectUtil
import vector.os.Group
import vector.util.GenericUtil
import kotlin.reflect.KClass

abstract class GroupAdapterEx<GROUP : Group<CHILD>, CHILD, VH : GroupViewHolder> :
    GroupMultiAdapterEx<GROUP, CHILD, VH>() {

    final override fun getGroupLayoutId(viewType: Int) = getGroupLayoutId()

    final override fun onBindGroupViewHolder(groupViewType: Int, item: GROUP, holder: VH) {
        onBindGroupViewHolder(item, holder)
    }

    abstract fun onBindGroupViewHolder(item: GROUP, holder: VH)

    final override fun getGroupType(groupPosition: Int): Int {
        return 0
    }

    final override fun getChildLayoutId(viewType: Int): Int = getChildLayoutId()

    final override fun onBindChildViewHolder(
        childViewType: Int,
        item: CHILD,
        holder: VH
    ) {
        onBindChildViewHolder(item, holder)
    }

    abstract fun onBindChildViewHolder(item: CHILD, holder: VH)

    final override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        return 0
    }

    abstract fun getGroupLayoutId(): Int
    abstract fun getChildLayoutId(): Int
}

abstract class GroupMultiAdapterEx<GROUP : Group<CHILD>, CHILD, VH : GroupViewHolder> :
    BaseExpandableListAdapter() {

    var data: List<GROUP>? = null
        set(value) {
            field = value
            value?.let {
                notifyDataSetChanged()
            }
        }

    @Suppress("UNCHECKED_CAST")
    private val holderClz: KClass<VH> by lazy {
        GenericUtil.getClassType(this::class, GroupViewHolder::class) as? KClass<VH>
            ?: throw IllegalStateException("can not find VH class")
    }

    override fun getGroupCount(): Int {
        return data?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): GROUP? {
        return data?.get(groupPosition)
    }

    abstract fun getGroupLayoutId(viewType: Int): Int

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val v: View
        if (convertView == null) {
            val holder = onCreateGroupViewHolder(groupPosition, parent)
            v = holder.itemView
            v.tag = holder
        } else {
            v = convertView
        }

        @Suppress("UNCHECKED_CAST")
        val holder = v.tag as VH
        val item = getGroup(groupPosition).throwIfNull("can not find GROUP item")
        onBindGroupViewHolder(getGroupType(groupPosition), item, holder)

        return v
    }

    open fun onCreateGroupViewHolder(groupPosition: Int, parent: ViewGroup?): VH {
        val type = getGroupType(groupPosition)
        val view = LayoutInflater.from(parent?.context).inflate(getGroupLayoutId(type), null)
        return ReflectUtil.newInst(holderClz, view).throwIfNull("can not create group view holder")
    }

    abstract fun onBindGroupViewHolder(groupViewType: Int, item: GROUP, holder: VH)

    override fun getChild(groupPosition: Int, childPosition: Int): CHILD? {
        return data?.get(groupPosition)?.getChildAt(childPosition)
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return getGroup(groupPosition)?.childrenCount ?: 0
    }

    abstract fun getChildLayoutId(viewType: Int): Int

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val v: View
        if (convertView == null) {
            val holder = onCreateChildViewHolder(groupPosition, childPosition, parent)
            v = holder.itemView
            v.tag = holder
        } else {
            v = convertView
        }

        @Suppress("UNCHECKED_CAST")
        val holder = v.tag as VH
        val item = getChild(groupPosition, childPosition).throwIfNull("can not find CHILD item")
        onBindChildViewHolder(getChildType(groupPosition, childPosition), item, holder)

        return v
    }

    open fun onCreateChildViewHolder(
        groupPosition: Int,
        childPosition: Int,
        parent: ViewGroup?
    ): VH {
        val type = getChildType(groupPosition, childPosition)
        val view = LayoutInflater.from(parent?.context).inflate(getChildLayoutId(type), null)
        return ReflectUtil.newInst(holderClz, view).throwIfNull("can not create group view holder")
    }

    abstract fun onBindChildViewHolder(childViewType: Int, item: CHILD, holder: VH)

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}

abstract class GroupViewHolder(val itemView: View)



