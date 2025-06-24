package dsb.design.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import eth.ext.bind
import lib.base.design.repo.UserRepo
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/29
 */
@HiltViewModel
class BindPhoneViewModel @Inject constructor(repo: UserRepo) :
    BaseInputPhoneViewModel(repo) {

    var wxUid: String? = null

    fun bindPhone() =
        repo.bindPhone(input.phone.value, input.captcha.value, wxUid).bind(user)
}