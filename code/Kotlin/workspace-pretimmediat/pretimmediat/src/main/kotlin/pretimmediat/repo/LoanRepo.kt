package pretimmediat.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import pretimmediat.ext.ensureUserIdFlow
import pretimmediat.model.loan.ContractInfo
import pretimmediat.model.loan.LoanApplyResult
import pretimmediat.model.loan.PerProduct
import pretimmediat.network.api.LoanApi
import pretimmediat.network.createApi
import javax.inject.Inject

@ViewModelScoped
class LoanRepo @Inject constructor() {

    fun fetchProduct(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<LoanApi>().products(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchPerProduct(
        userId: String?,
        ssid: String?,
        productId: String,
        detailId: String,
        applyAmount: String
    ): Flow<PerProduct> = ensureUserIdFlow(userId, ssid) {
        createApi<LoanApi>().fetchPerProduct(
            userId,
            ssid,
            productId,
            detailId,
            applyAmount
        )
    }.flowOn(Dispatchers.IO)

    fun fetchContractInfo(
        userId: String?,
        ssid: String?,
        detailId: String,
        applyAmount: String,
    ): Flow<List<ContractInfo>> = ensureUserIdFlow(userId, ssid) {
        createApi<LoanApi>().contractInfo(userId, ssid, applyAmount, detailId)
    }.flowOn(Dispatchers.IO)

    fun apply(
        userId: String?,
        ssid: String?,
        productId: String,
        detailId: String,
        applyAmount: String
    ): Flow<LoanApplyResult> = ensureUserIdFlow(userId, ssid) {
        createApi<LoanApi>().applyLoan(
            userId,
            ssid,
            productId,
            detailId,
            applyAmount
        )
    }.flowOn(Dispatchers.IO)
}