package pretimmediat.model.product

import com.google.gson.annotations.SerializedName
import pretimmediat.model.loan.InstallmentPlan

/**
 * 贷款计划, 包含还款和逾期类型
 *
 * @param orderNo 订单编号
 * @param settleStatus 0:未结清 ~~1:已结清
 * @param totalPeriod 总期数
 * @param plans 分期计划列表
 */
data class LoanPlan(
    @SerializedName("facialMountainousMessMinibusGesture")
    val orderNo: String,
    @SerializedName("regularBank")
    val settleStatus: String,
    @SerializedName("pacificSaltyMomentArmchair")
    val totalPeriod: String,
    @SerializedName("politeThickUpperEncouragement")
    val plans: List<InstallmentPlan>,
) {
    companion object {
        val TEST = LoanPlan(
            "3838349405059393933",
            "0",
            "3",
            listOf(
                InstallmentPlan.TEST,
                InstallmentPlan.TEST,
                InstallmentPlan.TEST,
                InstallmentPlan.TEST,
                InstallmentPlan.TEST,
            )
        )
    }
}