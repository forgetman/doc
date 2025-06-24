package pretimmediat.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import pretimmediat.network.api.LoginApi
import pretimmediat.network.createApi
import javax.inject.Inject

@ViewModelScoped
class UserRepo @Inject constructor() {

    fun fetchCaptcha(phoneNo: String) =
        createApi<LoginApi>().captcha(phoneNo).flowOn(Dispatchers.IO)

    fun login(phoneNo: String, code: String) =
        createApi<LoginApi>().login(phoneNo, code).flowOn(Dispatchers.IO)
}