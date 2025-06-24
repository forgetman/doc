package pretimmediat.model.product

import com.google.gson.annotations.SerializedName

/**
 * 还款/逾期页, 投诉内容
 * @param desc 底部描述文案
 * @param repayMobile 催收还款电话
 * @param repayWhatsApp 催收还款whatsapp
 * @param repayAlertDesc 催收还款描述文案
 * @param crispKey crispSDK的key
 */
data class Complaint(
    @SerializedName("learnedUmbrellaComfortableNonBoy")
    val desc: String?,
    @SerializedName("majorSaucer")
    val repayMobile: String?,
    @SerializedName("skillfulFavouritePineString")
    val repayWhatsApp: String?,
    @SerializedName("smoothVariousPoliceman")
    val repayAlertDesc: String?,
    @SerializedName("basicTrueSignature")
    val crispKey: String?,
    @SerializedName("usefulInlandHealth")
    val mobiles: List<ComplaintUnit>?,
    @SerializedName("goldenPity")
    val whatsApps: List<ComplaintUnit>?,
    @SerializedName("undividedPoliceHelicopterPole")
    val emails: List<ComplaintUnit>?,
)

data class ComplaintUnit(
    @SerializedName("strangePressurePianoEagerRing")
    val value: String?,
    @SerializedName("cubicHobbySweatYard")
    val type: String?,
)