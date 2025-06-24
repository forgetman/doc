package pretimmediat.model.loan

import com.google.gson.annotations.SerializedName
import pretimmediat.ext.formatMoney

/**
 * 试算
 *
 * @param preAmount 还款金额
 * @param disbursalAmount 到账金额
 * @param serviceFee 服务费
 * @param iva 增值税
 * @param intAmount 利息
 * @param ext 扩展字段
 */
data class PerProduct(
    @SerializedName("fullMessageTrackPepper")
    val preAmount: String,
    @SerializedName("messyDialogueLeftHoney")
    val disbursalAmount: String,
    @SerializedName("racialForeignElephantDizzySavage")
    val serviceFee: String,
    @SerializedName("shyMerryHandfulMillion")
    val iva: String,
    @SerializedName("safeSureLameSun")
    val intAmount: String,
    @SerializedName("messyAppointmentTractor")
    val ext: List<ProductExt>?,
    @SerializedName("politeThickUpperEncouragement")
    val resultMapPlanList: List<InstallmentPlan>?,
) {
    val preAmountText: String get() = preAmount.formatMoney()
    val disbursalAmountText: String get() = disbursalAmount.formatMoney()
    val serviceFeeText: String get() = serviceFee.formatMoney()
    val ivaText: String get() = iva.formatMoney()
    val intAmountText: String get() = intAmount.formatMoney()
}

/**
 * @param name 拓展字段名称
 * @param amount 拓展字段金额
 */
data class ProductExt(
    @SerializedName("swiftFactoryPossibleChineseMeans")
    val name: String?,
    @SerializedName("northernFairExtraCompetition")
    val amount: String?,
)

/**
 * @param repayPlanId 还款计划id
 * @param titleDate 时间文案
 * @param repayAmount 还款金额
 * @param loanAmount 贷款金额
 * @param repayDate 还款时间
 * @param titleAmount 金额文案
 * @param curDuration 当前期数
 * @param titleAmountRedMap 红色金额文案map
 * @param titleDateRedMap 红色时间文案map
 * @param settleStatus 0:未结清 1:已结清
 * @param lateFee 滞纳金
 * @param iva 税费
 * @param deductionFee 抵扣金额
 * @param title 标题
 * @param title2 时间标题
 * @param title3 金额标题
 * @param redTitleMap 红色标题map
 * @param redTitle2Map 红色时间标题map
 * @param redTitle3Map 红色金额标题map
 * @param afterExtendOrderAmt 展期费(延期费)
 * @param interest 利息
 * @param extendDate 展期已还款时间
 * @param totalRepay
 * @param selectMark 是否选中 1:选中 0:未选中
 */
data class InstallmentPlan(
    @SerializedName("halfAngryPrizeMercifulThem")
    val repayPlanId: String,
    @SerializedName("medicalMadPopcornPeople")
    val repayAmount: Float,
    @SerializedName("illPainterInlandTractor")
    val titleDate: String?,
    @SerializedName("swissViolinistConsiderateEgyptBitterInn")
    val repayDate: String?,
    @SerializedName("nuclearSummaryJustConductorPlanet")
    val titleAmount: String?,
    @SerializedName("bentCorrectionNormalAudienceBoundStand")
    val curDuration: String?,
    @SerializedName("broadStrictAntarcticaPunishment")
    val loanAmount: String?,
    @SerializedName("africanFortunateCourage")
    val titleAmountRedMap: Map<String, String>?,
    @SerializedName("hugeMarchScientificBothEveryone")
    val titleDateRedMap: Map<String, String>?,
    @SerializedName("regularBank")
    val settleStatus: String?,
    @SerializedName("taxHobbySocialQuarter")
    val lateFee: String?,
    @SerializedName("maleCourtEnjoyableReporter")
    val interest: String?,
    @SerializedName("shyMerryHandfulMillion")
    val iva: String?,
    @SerializedName("immediateSafetyMedicalStateEarlyDiamond")
    val deductionFee: String?,
    @SerializedName("unsuccessfulPurseCleaner")
    val title: String?,
    @SerializedName("crazyShadowRainbowMadBoss")
    val title2: String?,
    @SerializedName("finalFoot")
    val title3: String?,
    @SerializedName("internationalVegetableMoreFarmerUsualLoaf")
    val redTitleMap: Map<String, String>?,
    @SerializedName("radioactiveJustCleverRoof")
    val redTitle2Map: Map<String, String>?,
    @SerializedName("dustyTheyTobacco")
    val redTitle3Map: Map<String, String>?,
    @SerializedName("freezingBoothFlagSeriousJustice")
    val extendDuration: String,
    @SerializedName("certainMicrocomputerLegJoke")
    val extendDate: String,
    @SerializedName("africanMeatBigChocolate")
    val totalRepay: String,
    @SerializedName("northernDiplomaFirmBrideJournalist")
    val afterExtendOrderAmt: String,
    @SerializedName("marriedPoisonAppointmentIndianAdult")
    val selectMark: String,
) {
    companion object {
        const val STATUS_SETTLED = "1"
        const val STATUS_UN_SETTLED = "0"

        // 测试数据
        val TEST = InstallmentPlan(
            repayPlanId = "1",
            repayAmount = 1000f,
            titleDate = "2021-01-01",
            repayDate = "2021-01-01",
            titleAmount = "1000",
            curDuration = "1",
            loanAmount = "1000",
            titleAmountRedMap = mapOf("1" to "1000"),
            titleDateRedMap = mapOf("1" to "2021-01-01"),
            settleStatus = "0",
            lateFee = "0",
            interest = "0",
            iva = "0",
            deductionFee = "0",
            title = "title",
            title2 = "title2",
            title3 = "title3",
            redTitleMap = mapOf("1" to "title"),
            redTitle2Map = mapOf("1" to "title2"),
            redTitle3Map = mapOf("1" to "title3"),
            extendDuration = "1",
            extendDate = "2021-01-01",
            totalRepay = "1000",
            afterExtendOrderAmt = "0",
            selectMark = "0",
        )
    }

    val lateFeeNullableText: String? get() = if (lateFee == null || lateFee == "0") null else lateFee.formatMoney()
    val deductionFeeNullableText: String? get() = if (deductionFee == null || deductionFee == "0") null else deductionFee.formatMoney()
    val repayAmountText: String get() = repayAmount.formatMoney()
    val loanAmountText: String get() = loanAmount.formatMoney()
    val afterExtendOrderAmtText: String get() = afterExtendOrderAmt.formatMoney()
    val ivaText: String get() = iva.formatMoney()
    val interestText: String get() = interest.formatMoney()
}