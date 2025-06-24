package fund.design.repo

import eth.NiveString
import eth.bind
import fund.CommonApi
import fund.URL
import lib.base.NET
import lib.base.model.User

/**
 * @author yuansui
 * @since 2018/8/13
 */
class MeRepo {

    val avatar = NiveString()

    /**
     * 上传头像
     */
    fun uploadAvatar(path: String) =
        createApi(CommonApi::class).upload("avatar", path).bind(avatar)

    /**
     * 订单
     */
    fun orderUrl(): String {
        return URL.h5Host.plus("v2/order/orderState/dsb_user_token/")
            .plus(User.get().token)
            .plus("/navbar/1.html")
    }

    /**
     * 参保人
     */
    fun peopleUrl(): String {
        return URL.h5Host.plus("v2/user/canbaorenList?")
            .plus("alter=1&navbar=1&user_token=")
            .plus(User.get().token)
    }

    /**
     * 公积金
     */
    fun fundUrl(): String {
        return URL.h5Host.plus("v1/search/account/user_token/")
            .plus(User.get().token)
    }

    /**
     * 关于
     */
    fun aboutUrl(): String {
        return URL.h5Host.plus("v1/search/about.html")
    }
}