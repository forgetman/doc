package dsb.design.viewModel

import androidx.lifecycle.viewModelScope
import dsb.BuildConfig
import dsb.model.Input
import eth.ext.bind
import eth.model.Nive
import lib.base.design.repo.CaptchaType
import lib.base.design.repo.UserRepo
import lib.base.model.User
import vector.app.viewmodel.ViewModelEx
import vector.ext.hasSpecialSymbol
import vector.ext.isMobileCN
import vector.ext.toast

/**
 * @author yuansui
 * @since 2019/1/29
 */
abstract class BaseInputPhoneViewModel(protected val repo: UserRepo) : ViewModelEx() {

    val user = Nive<User>().apply {
        onError {
            toast(it.message)
        }
    }
    private val captcha = Nive<String>()

    val input = Input().apply {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEBUG_LOG) {
            phone.value = "18588760524"
            captcha.value = "666666"
        }
    }

    override fun onCreate() {
        user.observe {
            User.update(it)
        }
    }

    fun fetchCaptcha(type: CaptchaType) =
        repo.fetchCaptcha(input.phone.value, type)
            .bind(captcha)
            .launch(viewModelScope)

    /**
     * 检查是否满足登录条件
     */
    fun checkLoginEnabled(): Boolean {
        return checkPhone() && checkCaptcha()
    }

    fun checkPhone(): Boolean {
        val value = input.phone.value
        val valid = value != null && value.trim().length == 11 && value.isMobileCN()
        if (!valid) toast("手机格式不正确!")
        return valid
    }

    private fun checkCaptcha(): Boolean {
        val value = input.captcha.value
        val valid = value != null && value.isNotEmpty() && !value.hasSpecialSymbol()
        if (!valid) toast("验证码不正确")
        return valid
    }
}