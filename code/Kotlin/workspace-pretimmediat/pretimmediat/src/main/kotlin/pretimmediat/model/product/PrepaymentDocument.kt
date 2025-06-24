package pretimmediat.model.product

import com.google.gson.annotations.SerializedName

/**
 * 提前还款弹框文案
 */
data class PrepaymentDocument(
    @SerializedName("eitherComedy")
    val map: Map<String, String>,
    @SerializedName("enoughCamp")
    val documents: List<String>
)