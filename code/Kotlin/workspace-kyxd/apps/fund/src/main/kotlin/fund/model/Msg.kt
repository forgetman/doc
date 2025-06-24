package fund.model

import com.google.gson.annotations.SerializedName

/**
 * @author yuansui
 * @since 2018/8/1
 */
class Msg {
    @SerializedName("created_at")
    var date: String? = null // 04月24日,
    var content: String? = null // 测试数据大撒的撒大撒的撒大\n,

    @SerializedName("icon_url")
    var icon: String? = null // http, //\/\/m.dashebao.com\/v2\/assets\/images\/index_2.png,

    @SerializedName("jump_address")
    var url: String? = null
}

class ListMsg {
    var list: MutableList<Msg>? = null
}