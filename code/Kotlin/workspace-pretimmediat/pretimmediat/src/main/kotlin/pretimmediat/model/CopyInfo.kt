package pretimmediat.model

import com.google.gson.annotations.SerializedName

/**
 * @param appUserId 子产品用户id
 */
data class CopyInfo(
    @SerializedName("famousCenturyDateSame")
    val appUserId: String?
)