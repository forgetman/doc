package dsb.design.ui.adapter

import androidx.databinding.ViewDataBinding
import dsb.BR
import dsb.R
import dsb.model.GroupServProgress
import dsb.model.ServProgress
import vector.app.databinding.adapter.DBGroupMultiAdapterEx

class ServiceProgressAdapter : DBGroupMultiAdapterEx<GroupServProgress, ServProgress>() {

    companion object {
        const val VIEW_TYPE_TOP = 0
        const val VIEW_TYPE_MID = 1
        const val VIEW_TYPE_BOTTOM = 2
        const val VIEW_TYPE_SINGLE = 3
    }

    override fun getGroupLayoutId(viewType: Int): Int {
        return R.layout.layout_service_progress_item_group
    }

    override fun onBindGroupBinding(
        groupViewType: Int,
        item: GroupServProgress,
        binding: ViewDataBinding
    ) {
        binding.setVariable(BR.owner, this)
        binding.setVariable(BR.item, item)
    }

    override fun getChildLayoutId(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_TOP -> R.layout.layout_service_progress_item_child_top
            VIEW_TYPE_MID -> R.layout.layout_service_progress_item_child_mid
            VIEW_TYPE_BOTTOM -> R.layout.layout_service_progress_item_child_bottom
            else -> R.layout.layout_service_progress_item_child_single
        }
    }

    override fun onBindChildBinding(childViewType: Int, item: ServProgress, binding: ViewDataBinding) {
        binding.setVariable(BR.owner, this)
        binding.setVariable(BR.item, item)
    }

    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        if (getChildrenCount(groupPosition) == 1) return VIEW_TYPE_SINGLE

        return when (childPosition) {
            0 -> VIEW_TYPE_TOP
            getChildrenCount(groupPosition) - 1 -> VIEW_TYPE_BOTTOM
            else -> VIEW_TYPE_MID
        }
    }

    override fun getChildTypeCount(): Int {
        return 3
    }
}