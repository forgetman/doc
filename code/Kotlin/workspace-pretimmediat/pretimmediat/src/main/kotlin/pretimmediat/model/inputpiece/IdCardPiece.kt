package pretimmediat.model.inputpiece

import com.google.gson.annotations.SerializedName

/**
 * 进件页: idCard
 */
data class IdCardPiece(
    @SerializedName("latestChipsProudHarbour")
    val idNumber: String?,
)