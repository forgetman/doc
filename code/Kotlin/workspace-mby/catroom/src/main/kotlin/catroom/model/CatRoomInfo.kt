package catroom.model

import com.google.gson.annotations.SerializedName

data class CatRoomInfo(
    val id: Int,
    val status: Int,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("cat_room_id")
    val roomId: String,
    @SerializedName("cat_room_name")
    val roomName: String?,
    val longitude: String,
    val latitude: String,
    @SerializedName("iot_product_id")
    val iotProductId: String,
    @SerializedName("iot_device_name")
    val iotDeviceName: String,
    @SerializedName("iot_device_psk")
    val iotDevicePsk: String,
    @SerializedName("master_live_channel_id")
    val masterLiveChannelId: String,
    @SerializedName("master_live_push_url")
    val masterLivePushUrl: String,
    @SerializedName("side_live_channel_id")
    val sideLiveChannelId: String,
    @SerializedName("side_live_push_url")
    val sideLivePushUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
)