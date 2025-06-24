package dsb.network.api

import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import lib.base.model.Form

interface MeApi {
    @Post("v6/user/getMyList")
    fun list(@Query("city_id") cityId: String?): Flow<List<Form>>

    /**
     * 注销账号
     */
    @Post("v6/user/cancelUser")
    fun logout(@Query("code") code: String?): Flow<String>
}