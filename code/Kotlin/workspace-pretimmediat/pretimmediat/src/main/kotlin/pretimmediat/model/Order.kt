package pretimmediat.model

import com.google.gson.annotations.SerializedName
import pretimmediat.ext.formatMoney

/**
 * 订单
 * @param curUserId 子产品id
 * @param viewStatus 订单状态: 0-审核中, 1-拒绝, 2-订单完成, 3-逾期, 4-还款中, 5-放款失败,6-放款处理中
 * @param appName app名称
 * @param logoUrl logo
 * @param appSsid 当前产品 产品号
 * @param applyAmount 申请借款金额
 * @param submitTime 申请借款时间
 * @param repayAmt 还款金额
 * @param repayDate 还款日期
 * @param overdueDays 逾期天数, Expires% s days, Atrasado %s días-----用于逾期状态时间显示
 * @param extendDuration 延长期限, Incident+% s days of date, La fecha de\nvencimiento +%s días-----用于展期按钮时间显示
 * @param extendFlag 是否支持展期支付, 1:支持.
 * @param totalPeriod 总期数
 * @param notSettleCount 待还期数
 * @param reapplicationDate 重新申请日期
 */
data class Order(
    @SerializedName("extraordinaryPetThunderstormLateDusk")
    val orderId: String,
    @SerializedName("irishCafeThirstyTraining")
    val curUserId: String,
    @SerializedName("untrueSeveralPhysicsIndeedDialogue")
    val viewStatus: String,
    @SerializedName("guiltyDirtyVictoryDigestCancer")
    val appName: String,
    @SerializedName("egyptianSouthwestClockSurroundingScene")
    val logoUrl: String,
    @SerializedName("flatSuite")
    val appSsid: String?,
    @SerializedName("bestBedroomFairDeepConference")
    val applyAmount: String?,
    @SerializedName("eitherMankindFamiliarEngineerMorning")
    val submitTime: String?,
    @SerializedName("africanSomethingSaltyUnionPopularSeaman")
    val repayAmt: String?,
    @SerializedName("swissViolinistConsiderateEgyptBitterInn")
    val repayDate: String?,
    @SerializedName("nationalTailorDegreeCommunication")
    val overdueDays: String,
    @SerializedName("freezingBoothFlagSeriousJustice")
    val extendDuration: String,
    @SerializedName("crossJournalistRareBell")
    val extendFlag: String?,
    @SerializedName("pacificSaltyMomentArmchair")
    val totalPeriod: String,
    @SerializedName("convenientMerchantHumour")
    val notSettleCount: String,
    @SerializedName("tenseImportantMillionaireThoseSunlight")
    val reapplicationDate: String,
) {
    companion object {
        const val STATUS_IN_REVIEW = "0"
        const val STATUS_REJECT = "1"
        const val STATUS_COMPLETE = "2"
        const val STATUS_OVERDUE = "3"
        const val STATUS_REPAYING = "4"
        const val STATUS_PAY_FAILED = "5"
        const val STATUS_LOAN_PROCESSING = "6"
    }

    val applyAmountText: String get() = applyAmount.formatMoney()
}