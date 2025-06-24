package pretimmediat.model.inputpiece

import com.google.gson.annotations.SerializedName

/**
 * @param cardFrontFlag "1" 已上传身份证正面
 * @param cardFrontUrl 身份证正面图片url
 * @param faceVerified "1" 已上传人脸照片
 * @param faceUrl 人脸照片url(我的界面头像)
 */
data class IdCardImagePiece(
    @SerializedName("firmSilenceBasket")
    val cardFrontFlag: String?,
    @SerializedName("liquidPassageAboveBalloonJourney")
    val cardFrontUrl: String?,
    @SerializedName("actualMoralStick")
    val faceVerified: String?,
    @SerializedName("forgetfulBritishAccidentKingdom")
    val faceUrl: String?,
    @SerializedName("names")
    val givenName: String?,
)