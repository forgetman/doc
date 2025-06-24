package dsb.model

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 服务器值返回列表  包裹list类
 */
class PackList<T> {
    var list: List<T> = listOf()
}

fun <T> Flow<PackList<T>>.unpackList(): Flow<List<T>> {
    return map { it.list }
}

class PackGpsCity {
    @SerializedName("city_info")
    var city: City? = null
}