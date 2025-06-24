package pretimmediat.model.loan

import com.google.gson.annotations.SerializedName

/**
 * @param rbFlag "1": 复贷.
 * @param pointSubmitFlag "1"是需要打(首贷申请成功)点
 */
data class LoanApplyResult(
    @SerializedName("extraordinaryPetThunderstormLateDusk")
    val orderId: String,
    @SerializedName("facialMountainousMessMinibusGesture")
    val orderNo: String,
    @SerializedName("happyServiceGrandLid")
    val contractList: List<Contract>,
    @SerializedName("youngMemoryLowBread")
    val rbFlag: String,
    @SerializedName("practicalRulerUnsuccessfulAccident")
    val pointSubmitFlag: String,
) {
    /**
     * 主产品首贷申请成功
     */
    fun isFirstApply() = pointSubmitFlag == "1"
}

data class Contract(
    @SerializedName("swiftFactoryPossibleChineseMeans")
    val name: String,
    @SerializedName("freeSweatTelegramSplendidFall")
    val url: String,
)