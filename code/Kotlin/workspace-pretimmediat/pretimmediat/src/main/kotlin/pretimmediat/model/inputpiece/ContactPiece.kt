package pretimmediat.model.inputpiece

import com.google.gson.annotations.SerializedName

/**
 * 进件页: 联系人
 * @param rsName 第一联系人 关系
 * @param relationshipCode 第一联系人 关系对应的code
 */
data class ContactPiece(
    @SerializedName("aggressiveSadnessDeafIllness")
    val phoneNumber: String,
    @SerializedName("swiftFactoryPossibleChineseMeans")
    val name: String,
    @SerializedName("thickCollege")
    val rsName: String,
    @SerializedName("fondInstrument")
    val relationshipCode: String,
    @SerializedName("naturalHarmfulNestInlandHong")
    val pnSec: String,
    @SerializedName("racialLectureMid")
    val nameSec: String,
    @SerializedName("unusualAuthorPyramidGovernment")
    val rsSecName: String,
    @SerializedName("excellentMarriagePolitics")
    val rsSecCode: String,
)