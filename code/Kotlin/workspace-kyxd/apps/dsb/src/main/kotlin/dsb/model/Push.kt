package dsb.model

import com.google.gson.annotations.SerializedName

/**
 * @author yuansui
 * @since 2019/2/26
 */
class Push {
    @SerializedName("pageidx")
    var pageIdx: Int? = null

    @SerializedName("opentype")
    var openType: Int? = null

    @SerializedName("openparm")
    var url: String? = null
}