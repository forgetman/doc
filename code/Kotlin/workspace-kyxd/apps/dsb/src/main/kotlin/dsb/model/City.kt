package dsb.model

import com.google.gson.annotations.SerializedName
import dsb.SpApp
import dsb.design.ui.adapter.CityViewType
import vector.EMPTY
import vector.bindingadapter.GridLayoutSet
import vector.os.Group

/**
 * @author yuansui
 * @since 2019/1/22
 */
class GroupCity : Group<City>() {
    var name: String = EMPTY
}

open class City {
    @SerializedName("city_id")
    var id: String? = null

    @SerializedName("city_name")
    var name: String? = null

    @SerializedName("pinyin")
    var spell: String? = null

    var type: Int? = null
}

class CityHot : City() {

    init {
        type = CityViewType.HOT.ordinal
    }

    @Transient
    var sets: List<GridLayoutSet>? = null
}

object GpsCity {
    var id: String? = null
    var name: String? = null
        get() = if (field.isNullOrEmpty()) "定位中" else field

    fun save() {
        SpApp.put(SpApp.KEY_GPS, City().apply {
            id = this@GpsCity.id
            name = this@GpsCity.name
        })
    }

    fun reset() {
        val cache = SpApp.getGpsCity()
        id = if (cache == null) {
            "110100"
        } else {
            cache.id
        }
        name = "定位中"
    }

    fun clear() {
        id = null
        name = null
    }
}