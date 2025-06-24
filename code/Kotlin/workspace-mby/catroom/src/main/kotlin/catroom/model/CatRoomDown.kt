package catroom.model

import com.google.gson.annotations.SerializedName

data class CatRoomDown(
    val feeding: String?,
    @SerializedName("quantity_of_electricity")
    val quantityOfElectricity: Int,
    @SerializedName("light_status")
    private val lightStatus: Int?,
    @SerializedName("ambient_type")
    val ambientType: Int?,
    @SerializedName("induction_type")
    val inductionType: Int?,
    @SerializedName("food_cat_type")
    val foodCatType: Int?,
    /**
     * 实际是机器重启
     */
    private val restart: Int?,
    private val reboot: Int?,
    private val init: Int?,
    private val renew: Int?,
    @SerializedName("upload_log")
    private val uploadLog: Int?,
    @SerializedName("upload_all_log")
    private val uploadAllLog: Int?,
) {
    fun shouldLightOn(): Boolean {
        return lightStatus == 1
    }

    fun shouldLightOff(): Boolean {
        return lightStatus == 0
    }

    fun shouldRestart(): Boolean {
        return restart == 1
    }

    fun shouldReboot(): Boolean {
        return reboot == 1
    }

    fun shouldInit(): Boolean {
        return init == 1
    }

    fun shouldRenew(): Boolean {
        return renew == 1
    }

    fun shouldUploadLog(): Boolean {
        return uploadLog == 1
    }

    fun shouldUploadAllLog(): Boolean {
        return uploadAllLog == 1
    }
}