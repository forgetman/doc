package pretimmediat.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import pretimmediat.ext.ensureUserIdFlow
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2024/7/11
 */
@ViewModelScoped
class RepayingOrOverdueRepo @Inject constructor() {
    fun fetchPayChannels(userId: String?, ssid: String?, type: String) =
        ensureUserIdFlow(userId, ssid) {
            createApi<ProductApi>().checkPayChannels(userId, ssid, type)
        }.flowOn(Dispatchers.IO)

    fun fetchLoanPlan(userId: String?, ssid: String?, orderId: String) =
        ensureUserIdFlow(userId, ssid) {
            createApi<ProductApi>().checkLoanPlan(userId, ssid, orderId, "0304")
        }.flowOn(Dispatchers.IO)

    fun fetchComplaintText(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<ProductApi>().complaintText(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchPrepaymentDocument(userId: String?, ssid: String?, orderId: String) =
        ensureUserIdFlow(userId, ssid) {
            createApi<ProductApi>().prepaymentDocument(userId, ssid, orderId)
        }.flowOn(Dispatchers.IO)
}