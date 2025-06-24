package fund.model

import androidx.annotation.DrawableRes
import fund.R

enum class MeLayoutStyle {
    TEXT,
    DIVIDER,
    LOGOUT,
}

enum class MeType(val text: String, @DrawableRes val drawableId: Int) {
    ORDER("我的公积金", R.drawable.me_ic_fund),
    DIFF("我的代缴订单", R.drawable.me_ic_order),
    PERSON("我的参保人", R.drawable.me_ic_people),
    UPLOAD("推荐app给好友", R.drawable.me_ic_recommend),
    STORE("给我们好评", R.drawable.me_ic_good),
    ABOUT("关于快查公积金", R.drawable.me_ic_about),
    DIVIDER("分割线", 0),
    LOGOUT("退出登录", 0),
}

data class Me(val style: MeLayoutStyle, val type: MeType)