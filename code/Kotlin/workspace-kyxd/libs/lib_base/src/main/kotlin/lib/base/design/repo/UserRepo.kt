package lib.base.design.repo

import dagger.hilt.android.scopes.ViewModelScoped
import lib.base.UserApi
import lib.base.network.createApi
import javax.inject.Inject

enum class CaptchaType(val id: Int) {
    SNS(1),
    CALL(2)
}

@ViewModelScoped
class UserRepo @Inject constructor() {

    fun fetchCaptcha(phone: String?, type: CaptchaType) =
        createApi<UserApi>().captcha(phone, type.id)

    fun login(phone: String?, captcha: String?) =
        createApi<UserApi>().login(phone, captcha)

    fun loginWechat(info: String) =
        createApi<UserApi>().loginWechat(info)

    fun bindPhone(phone: String?, captcha: String?, wxUid: String?) =
        createApi<UserApi>().bindPhone(phone, captcha, wxUid)
}