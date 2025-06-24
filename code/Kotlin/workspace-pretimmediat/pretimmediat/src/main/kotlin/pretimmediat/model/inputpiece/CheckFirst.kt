package pretimmediat.model.inputpiece

import com.google.gson.annotations.SerializedName

/**
 * 进件页首次打点信息
 */
data class CheckFirst(
    @SerializedName("skillfulDictionaryFamily")
    val idCardFirst: String,
    @SerializedName("electricalIndeedFalseClothing")
    val bankFirst: String
) {
    /**
     * 主产品首次保存身份信息
     */
    fun isIdCardFirst(): Boolean {
        return idCardFirst == "1"
    }

    /**
     * 主产品首次保存银行卡信息
     */
    fun isBankFirst(): Boolean {
        return bankFirst == "1"
    }
}