package dsb.design.ui.adapter

import androidx.databinding.ViewDataBinding
import dsb.BR
import dsb.R
import dsb.model.City
import dsb.model.GroupCity
import vector.app.databinding.adapter.DBGroupMultiAdapterEx

enum class CityViewType(val desc: String) {
    LOCATION("定位"), // 定位
    HOT("热门"), // 热门
    NORMAL("普通") // 普通
}

/**
 * @author yuansui
 * @since 2019/1/22
 */
class CityAdapter : DBGroupMultiAdapterEx<GroupCity, City>() {

    override fun getGroupLayoutId(viewType: Int): Int {
        return R.layout.layout_city_item_group
    }

    override fun onBindGroupBinding(groupViewType: Int, item: GroupCity, binding: ViewDataBinding) {
        binding.setVariable(BR.item, item)
    }

    override fun getChildLayoutId(viewType: Int): Int {
        return when (viewType) {
            CityViewType.HOT.ordinal -> R.layout.layout_city_item_child_hot
            else -> R.layout.layout_city_item_child
        }
    }

    override fun onBindChildBinding(childViewType: Int, item: City, binding: ViewDataBinding) {
        binding.setVariable(BR.item, item)
    }

    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        return getChild(groupPosition, childPosition)?.type ?: CityViewType.NORMAL.ordinal
    }

    override fun getChildTypeCount(): Int {
        return CityViewType.values().size
    }
}