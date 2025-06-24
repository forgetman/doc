package dsb.design.repo

import android.widget.TextView
import dsb.Bus
import dsb.EventId
import dsb.R
import dsb.design.ui.adapter.CityViewType
import dsb.model.City
import dsb.model.CityHot
import dsb.model.GpsCity
import dsb.model.GroupCity
import dsb.network.api.CityApi
import kotlinx.coroutines.flow.map
import lib.base.network.createApi
import vector.bindingadapter.GridLayoutSet
import vector.ext.copyFields
import vector.ext.move
import vector.app.util.Screen
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/22
 */
class CityRepo @Inject constructor() {

    fun fetchCities() =
        createApi<CityApi>()
            .list()
            .map { map ->
                val list = map
                    .map {
                        GroupCity().apply {
                            name = it.key
                            children = it.value
                        }
                    }
                    .sortedBy { it.name }
                    .toMutableList()

                // 排序以后热门在最后
                val hot = list.last()
                // 做下校验
                if (hot.name == CityViewType.HOT.desc) {
                    val orgHots = hot.children
                    // 生成单个保存了所有热门城市的city
                    val singleHot = CityHot().apply {
                        copyFields(hot)

                        this.sets = orgHots.map {
                            GridLayoutSet().apply {
                                id = R.layout.layout_city_hot_item
                                layoutType = GridLayoutSet.LayoutType.average(Screen.width)

                                onDataSet = { view ->
                                    val tv = view.findViewById<TextView>(R.id.city_hot_item_tv_name)
                                    tv.text = it.name
                                }

                                onClick = { _ ->
                                    Bus.get().send(EventId.CHANGE_CITY, it)
                                }
                            }
                        }

                    }

                    hot.children = mutableListOf(singleHot)

                    list.move(list.lastIndex, 0)
                }

                /*
                加入定位城市
                 */
                val locationGc = GroupCity().apply {
                    name = CityViewType.LOCATION.desc
                    children = mutableListOf(City().apply {
                        id = GpsCity.id
                        name = GpsCity.name
                        type = CityViewType.LOCATION.ordinal
                    })
                }
                list.add(0, locationGc)

                list
            }
}