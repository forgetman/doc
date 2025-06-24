package pretimmediat.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import pretimmediat.ext.ensureUserIdFlow
import pretimmediat.model.Order
import pretimmediat.network.api.OrderApi
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import javax.inject.Inject

/**
 * 订单数据仓库
 */
@ViewModelScoped
class OrderRepo @Inject constructor() {

    private val repayingOrOverdueRepo = RepayingOrOverdueRepo()

    fun fetchOrders(): Flow<List<Order>> = createApi<OrderApi>().orders()

    fun fetchLoanPlan(userId: String?, ssid: String?, orderId: String) =
        repayingOrOverdueRepo.fetchLoanPlan(userId, ssid, orderId)

    fun fetchPayChannels(userId: String?, ssid: String?, type: String) =
        repayingOrOverdueRepo.fetchPayChannels(userId, ssid, type)

    fun fetchInstallment(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<ProductApi>().singleInstallment(userId, ssid)
    }

}