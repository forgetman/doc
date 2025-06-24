package pretimmediat.model.loan

import com.google.gson.annotations.SerializedName

/**
 * 贷款商品信息
 */
data class LoanProducts(
    @SerializedName("chemicalBarEndlessFactorySunnyIndia")
    val productId: String,
    @SerializedName("giftedCrossingStrangePotElectricalSorrow")
    val serverTime: String,
    @SerializedName("friendlyCrazyBedroom")
    val proDetailList: List<ProductDetail>?,
)

/**
 * @param duration 每期天数
 * @param stepAmount 步长金额
 * @param maxCAmount 最大金额
 * @param minCAmount 最小金额
 * @param periodDuration 期数
 */
data class ProductDetail(
    @SerializedName("thankfulSightThinBarbershopMutton")
    val stepAmount: Int,
    @SerializedName("classicalTomorrowIllPlayroom")
    val detailId: String,
    @SerializedName("greenSeriousBroadcastClassroom")
    val duration: Int,
    @SerializedName("lateReligiousDrunkKite")
    val maxCAmount: Int,
    @SerializedName("aggressiveTermShowerPrivateAugust")
    val minCAmount: Int,
    @SerializedName("valuableElectricalSunnyMemorial")
    val periodDuration: Int,
    @SerializedName("seriousBadCreamLeftSteam")
    val rate: Float,
)

data class Installment(val id: String, val period: Int, val day: Int, val available: Boolean)

fun ProductDetail.toInstallment(available: Boolean): Installment {
    val day = periodDuration * duration
    return Installment(detailId, periodDuration, day, available)
}