package fund.design.viewModel.user

import fund.Bus
import fund.EventId
import lib.base.design.repo.CaptchaType
import lib.base.design.repo.UserRepo
import vector.design.viewModel.ViewModelEx

/**
 * @author yuansui
 * @since 2018/8/6
 */
class PhoneInputViewModel : ViewModelEx() {

    private val repo = UserRepo()

    val captcha = repo.captchaState

    init {
        repo.data.observe {
            Bus.get().send(EventId.LOGIN)
        }
    }

    fun getCaptcha(phone: String, type: CaptchaType) =
        repo.getCaptcha(phone, type)

    fun login(phone: String, captcha: String) =
        repo.login(phone, captcha)

    fun loginWechat(info: String) =
        repo.loginWechat(info)

    fun bindPhone(phone: String, captcha: String, wxUid: String) =
        repo.bindPhone(phone, captcha, wxUid)
}