package catroom.model

import com.google.gson.annotations.SerializedName

/**
 * @param upgradation 是否允许更新 1为允许
 * @param url 更新地址
 */
data class Upgrade(
    @SerializedName("need_upgradation")
    val upgradation: Int,
    @SerializedName("apk_url")
    val url: String?,
) {
    fun isEnable() = upgradation == 1
}