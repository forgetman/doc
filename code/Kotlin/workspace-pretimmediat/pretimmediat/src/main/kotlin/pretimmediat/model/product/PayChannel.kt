package pretimmediat.model.product

import com.google.gson.annotations.SerializedName

/**
 * 支付通道
 * @param channelCode 支付通道code,
 * @param channelName 支付通道名称
 * @param logoUrl 支付通道logo图片地址
 * @param browserOpen 为1时浏览器打开
 */
data class PayChannel(
    @SerializedName("englishNurseRegularHandwritingPupil")
    val channelCode: String,
    @SerializedName("rainyUniformCertainCourage")
    val channelName: String,
    @SerializedName("upperMinuteLessChickChemistry")
    val logoUrl: String,
    @SerializedName("centralClinicAnotherDriverRedCrop")
    val browserOpen: String,
) {
    companion object {
        val TEST = PayChannel(
            "testCode",
            "testName",
            "testUrl",
            "1"
        )
    }
}