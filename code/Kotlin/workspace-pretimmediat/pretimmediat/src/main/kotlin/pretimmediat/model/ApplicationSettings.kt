package pretimmediat.model

import com.google.gson.annotations.SerializedName
import pretimmediat.ext.formatMoney

/**
 * @param maxCAmount 首页-最大金额
 * @param maxCAmountTest 首页-最大金额-测试
 * @param maxDay 首页-最大天数
 * @param maxDayTest 首页-最大天数-测试
 * @param repayingRepaymentToggle 还款中-提前还款开关
 * @param repayingComplaintToggle 还款中-投诉开关
 * @param repayingComplaintAutoPopupToggle 还款中-投诉自动弹窗开关
 */
data class ApplicationSettings(
    @SerializedName("lateReligiousDrunkKite")
    val maxCAmount: String,
    @SerializedName("deadTermEndlessProgress")
    val maxCAmountTest: String,
    @SerializedName("aloneSaleswomanMyself")
    val maxDay: String,
    @SerializedName("nextBooth")
    val maxDayTest: String,
    @SerializedName("lazyPercentageLiteraryBowlBrownFibre")
    val repayingRepaymentToggle: String,
    @SerializedName("foreignBottleYellowFloor")
    val repayingComplaintToggle: String,
    @SerializedName("preciousRoundaboutHometown")
    val repayingComplaintAutoPopupToggle: String,
) {
    fun isRepayingRepaymentToggle() = repayingRepaymentToggle == "1"
    fun isRepayingComplaintToggle() = repayingComplaintToggle == "1"
    fun isRepayingComplaintAutoPopupToggle() = repayingComplaintAutoPopupToggle == "1"

    val maxCAmountText: String
        get() = maxCAmount.formatMoney()

    val maxCAmountTestText: String
        get() = maxCAmountTest.formatMoney()
}