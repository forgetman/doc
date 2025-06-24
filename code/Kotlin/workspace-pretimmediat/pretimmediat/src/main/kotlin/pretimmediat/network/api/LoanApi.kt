package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.method.Post
import eth.annotation.param.Header
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.loan.ContractInfo
import pretimmediat.model.loan.LoanApplyResult
import pretimmediat.model.loan.LoanProducts
import pretimmediat.model.loan.PerProduct
import pretimmediat.network.ParamsName

interface LoanApi {

    @Post("/slice/deleteFinalLength")
    fun products(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?
    ): Flow<LoanProducts>

    /**
     * @param productId 选择的商品id
     * @param detailId 选择的产品详情id
     * @param applyAmount 选择申请的金额
     */
    @Post("/slice/impossibleContinentToothIndependentSkyscraper")
    fun fetchPerProduct(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("chemicalBarEndlessFactorySunnyIndia") productId: String,
        @Query("classicalTomorrowIllPlayroom") detailId: String,
        @Query("bestBedroomFairDeepConference") applyAmount: String,
    ): Flow<PerProduct>

    /**
     * 申请贷款
     */
    @Post("/slice/americanBathrobeLikelyAnnouncement")
    fun applyLoan(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("chemicalBarEndlessFactorySunnyIndia") productId: String,
        @Query("classicalTomorrowIllPlayroom") detailId: String,
        @Query("bestBedroomFairDeepConference") applyAmount: String,
    ): Flow<LoanApplyResult>

    /**
     * 获取合同信息
     * @param orderType 默认分期0、真分期1
     */
    @Post("/anxiousBit/glareSocialistCorner")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun contractInfo(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("bestBedroomFairDeepConference") applyAmount: String,
        @Query("classicalTomorrowIllPlayroom") detailId: String,
        @Query("goodBirdSubtraction") orderType: String = "1",
    ): Flow<List<ContractInfo>>
}