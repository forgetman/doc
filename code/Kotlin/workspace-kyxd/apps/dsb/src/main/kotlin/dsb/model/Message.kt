package dsb.model

import com.google.gson.annotations.SerializedName
import eson.Ignore
import live.Live

/**
 * @author yuansui
 * @since 2019/1/23
 */
class InfoMessage {
    enum class Type {
        SYSTEM,
        CUSTOMER
    }

    @SerializedName("created_at")
    var time: String? = null
    var title: String? = null
    var content: String? = null

    @SerializedName("icon_url")
    var icon: String? = null

    var type: Type? = null

    @Ignore
    val unreadNumber = Live(0)
}

class DetailMessage {
    @SerializedName("created_at")
    var time: String? = null

    var title: String? = null
    var content: String? = null

    @SerializedName("icon_url")
    var icon: String? = null

    @SerializedName("jump_address")
    var url: String? = null
}

class HomeMessage {
    @SerializedName("img_url")
    var icon: String? = null

    @SerializedName("jump_address")
    val url: String? = null
}

class UnreadMessage {
    var num: Int = 0
}