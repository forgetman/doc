package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.method.Post
import eth.annotation.param.Header
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.MultiProduct
import pretimmediat.model.product.Complaint
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.PayChannel
import pretimmediat.model.product.PayInfo
import pretimmediat.model.product.PrepaymentDocument
import pretimmediat.model.product.SingleBanner
import pretimmediat.model.product.SingleProduct
import pretimmediat.network.ParamsName

interface ProductApi {

    @Post("/slice/compareNewLesson")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun mulAppInstallment(): Flow<List<MultiProduct>>

    /**
     * 单产品状态接口
     */
    @Post("/tightThirst/linkIndeedMother")
    fun singleInstallment(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("luckyBirdEdge") mulFlag: String = "1"
    ): Flow<SingleProduct>

    /**
     * 单产品banner
     */
    @Post("/tightThirst/layElectricalSalt")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun singleBanner(@Query("extraordinaryJarPrimaryLocalPicture") imageType: String = "03"): Flow<List<SingleBanner>>

    @Post("/slice/greekNervousLanguageEnglishSightseeing")
    fun checkLoanPlan(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("extraordinaryPetThunderstormLateDusk") orderId: String,
        @Query("eastGratefulContraryCrime") payType: String
    ): Flow<LoanPlan>

    /**
     * @param type 还款中和逾期默认"00". 展期"0304"
     */
    @Post("/leftoverCitizen/catchAsianBanana")
    fun checkPayChannels(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("cubicHobbySweatYard") type: String
    ): Flow<List<PayChannel>>

    /**
     * 投诉内容
     */
    @Post("/tightThirst/careRapidShot")
    fun complaintText(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
    ): Flow<Complaint>

    @Post("/differentDetective/spanishCheerfulGayPossibility")
    fun prepaymentDocument(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("extraordinaryPetThunderstormLateDusk") orderId: String,
        @Query("shortMessageMistakenBrick") configKey: String = "documents_1",
        @Query("goodBirdSubtraction") orderType: String = "1"
    ): Flow<PrepaymentDocument>

    /**
     * 获取还款链接
     * @param payType 还款中和逾期默认"00". 展期"0304"
     */
    @Post("/slice/safeGardenEveryoneDescription")
    fun payInfoInstallment(
        @Query("extraordinaryPetThunderstormLateDusk") orderId: String,
        @Query("englishNurseRegularHandwritingPupil") payChannelCode: String,
        @Query("normalSwordUnhealthyRadium") repayPlanIdList: String,
        @Query("eastGratefulContraryCrime") payType: String
    ): Flow<PayInfo>
}