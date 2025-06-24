package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.AppInfo
import pretimmediat.network.ParamsName

interface DeviceApi {

    @Post("/tightThirst/passPrivateBowl")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun appInfo(
        @Query("electricAdventureCarefulFairNeighbour") versionName: String,
        @Query("smokeCustomerDiscountMercifulBeast") versionCode: String,
        @Query("gentleFingernail") imei: String
    ): Flow<AppInfo>
}