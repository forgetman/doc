package pretimmediat.model.product

import com.google.gson.annotations.SerializedName
import pretimmediat.ext.formatMoney

/**
 * 首页-单产品审核状态
 * @param loanStatus
 * @param odStatus 逾期状态
 * @param reapplyDate 拒绝状态中可申请时间
 * @param reapplicationDays 拒绝状态中的天数
 * @param applyAmt 申请金额
 * @param repayAmt 还款金额
 * @param orderNo 订单编号
 * @param orderId 订单id
 * @param submitTime 申请时间
 * @param extendFlag 是否支持展期支付 1:支持
 * @param extendDuration 展期天数
 * @param overdueDays 逾期天数
 * @param repaymentDate 还款日期
 */
data class SingleProduct(
    @SerializedName("tastelessBehaviourInternationalLooseMailbox")
    val loanStatus: Int,
    @SerializedName("pleasantSelfRadiumHers")
    val odStatus: Int,
    @SerializedName("tenseImportantMillionaireThoseSunlight")
    val reapplyDate: String?,
    @SerializedName("extraordinaryPetThunderstormLateDusk")
    val orderId: String,
    @SerializedName("facialMountainousMessMinibusGesture")
    val orderNo: String,
    @SerializedName("cubicValueTaxSocialism")
    val applyAmt: String?,
    @SerializedName("africanSomethingSaltyUnionPopularSeaman")
    val repayAmt: String?,
    @SerializedName("eachCoolShabbyHive")
    val reapplicationDays: String?,
    @SerializedName("eitherMankindFamiliarEngineerMorning")
    val submitTime: String?,
    @SerializedName("crossJournalistRareBell")
    val extendFlag: String?,
    @SerializedName("freezingBoothFlagSeriousJustice")
    val extendDuration: String?,
    @SerializedName("nationalTailorDegreeCommunication")
    val overdueDays: String,
    @SerializedName("fondCriminalChangeableLetter")
    val repaymentDate: String,
) {
    companion object {
        const val OD_STATUS_REPAYING = 0
        const val OD_STATUS_OVERDUE = 1
        const val OD_STATUS_IN_REVIEW = 2
        const val OD_STATUS_REJECT = 3
        const val OD_STATUS_CAN_APPLY = -1

        const val STATUS_FAILED = 2
        const val STATUS_PROCESSING = 3

        val TEST = SingleProduct(
            0,
            0,
            "2021-08-01",
            "test",
            "test",
            "1000",
            "1000",
            "3",
            "2021-08-01",
            "1",
            "7",
            "3",
            "2021-08-01"
        )
    }

    val applyAmtText: String
        get() = applyAmt.formatMoney()
}