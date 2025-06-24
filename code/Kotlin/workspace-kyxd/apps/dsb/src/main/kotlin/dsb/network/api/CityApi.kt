package dsb.network.api

import dsb.model.City
import dsb.model.PackGpsCity
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow

interface CityApi {

    /**
     * 城市列表
     */
    @Post("v4/city/getCitys")
    fun list(): Flow<Map<String, MutableList<City>>>

    /**
     * 定位
     */
    @Post("v4/city/getLocationCity")
    fun gps(
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Flow<PackGpsCity>
}