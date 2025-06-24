package pretimmediat.model

import com.google.gson.annotations.SerializedName

/**
 * @param newCustFlag "1" 为首次注册成功
 * @param testCustFlag "1" 为测试账号
 */
data class AccountInfo(
    @SerializedName("roughPoorGlue")
    val account: String,
    @SerializedName("emptyConservationPoliticalNailCivilHaircut")
    val token: String,
    @SerializedName("coolThinkingHelpfulLawyerNiece")
    val newCustFlag: String,
    @SerializedName("drunkCompetitorSwissTibet")
    val testCustFlag: String
)