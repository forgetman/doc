package lib.base.model

import com.google.gson.annotations.SerializedName

/**
 * @author yuansui
 * @since 2019/1/29
 */
open class Form {
    @SerializedName("view_type")
    var viewType: Int = 0

    var title: String? = null// 我的订单,

    @SerializedName("subtitle")
    var subTitle: String? = null// 我的订单,

    var track: String? = null // my_order,
    var icon: String? = null // 图标
    var url: String? = null // http:\/\/testm.luobohr.com\/dsbapi\/v2\/order\/orderState,
    var content: String? = null

    @SerializedName("need_login")
    var needLogin: Boolean = false // true

    var btn: String? = null
    var img: String? = null // 图标
    var desc: String? = null
    var date: String? = null
    var name: String? = null
    var balance: String? = null
    var num: String? = null
    var range: String? = null

    @SerializedName("apply_num")
    var applyNumber: String? = null

    var list: MutableList<Form>? = null
}