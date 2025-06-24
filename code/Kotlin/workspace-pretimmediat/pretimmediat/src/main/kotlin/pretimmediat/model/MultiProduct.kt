package pretimmediat.model

import com.google.gson.annotations.SerializedName
import pretimmediat.ext.formatMoney

/**
 * 首页 多产品模型
 * @param maxCAmount 可申请金额
 * @param appName app名称
 * @param logoUrl app logo地址
 * @param viewStatus 产品状态: 0-可申请, 1-还款中, 2-逾期, 3-审核中, 4-拒绝, 5-放款失败,6-放款处理中, 8-额度用尽
 * @param curUserId 当前产品 userId
 * @param orderId 订单ID
 * @param appSsid 当前产品 产品号
 */
data class MultiProduct(
    @SerializedName("lateReligiousDrunkKite") val maxCAmount: String?,
    @SerializedName("guiltyDirtyVictoryDigestCancer") val appName: String?,
    @SerializedName("egyptianSouthwestClockSurroundingScene") val logoUrl: String?,
    @SerializedName("untrueSeveralPhysicsIndeedDialogue") val viewStatus: Int?,
    @SerializedName("irishCafeThirstyTraining") val curUserId: String?,
    @SerializedName("extraordinaryPetThunderstormLateDusk") val orderId: String?,
    @SerializedName("flatSuite") val appSsid: String?,
) {
    companion object {
        const val STATUS_CAN_APPLY = 0
        const val STATUS_REPAYING = 1
        const val STATUS_OVERDUE = 2
        const val STATUS_IN_REVIEW = 3
        const val STATUS_REJECT = 4
        const val STATUS_PAY_FAILED = 5
        const val STATUS_LOAN_PROCESSING = 6
        const val STATUS_NO_QUOTA = 8
    }

    val maxCAmountText: String get() = maxCAmount.formatMoney()
}