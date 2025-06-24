package dsb.design.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eth.ext.bind
import lib.base.design.repo.UserRepo
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/23 0023
 */
@HiltViewModel
class SignInViewModel @Inject constructor(repo: UserRepo) : BaseInputPhoneViewModel(repo) {

    fun login() =
        repo.login(input.phone.value, input.captcha.value)
            .bind(user)
            .launch(viewModelScope)

    fun loginWechat(info: String) =
        repo.loginWechat(info).bind(user).launch(viewModelScope)
}