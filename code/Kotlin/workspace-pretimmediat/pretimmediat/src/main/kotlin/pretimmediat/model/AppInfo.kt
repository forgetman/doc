package pretimmediat.model

import com.google.gson.annotations.SerializedName

/**
 * @param forceUpdateFlag 是否强制更新 1：强制更新 ，2：用户可选择性更新
 */
data class AppInfo(
    @SerializedName("centigradeThinPercentageSpokenNephew")
    val forceUpdateFlag: String,
    @SerializedName("tenseHoleExistenceEnglishSoup")
    val promptMsg: String,
    @SerializedName("roundHabitFrenchmanLatestSpaghetti")
    val appDownUrl: String
)