package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.method.Post
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.Order
import pretimmediat.network.ParamsName

interface OrderApi {

    /**
     * 多产品订单
     */
    @Post("/slice/plasticSisterComfortableFlamingGlue")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun orders(): Flow<List<Order>>
}