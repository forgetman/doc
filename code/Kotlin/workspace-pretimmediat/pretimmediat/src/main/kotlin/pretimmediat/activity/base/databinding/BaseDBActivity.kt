package pretimmediat.activity.base.databinding

import androidx.annotation.CallSuper
import inject.annotation.creator.Extra
import pretimmediat.viewmodel.BaseViewModel
import vector.app.databinding.activity.DBActivityEx

abstract class BaseDBActivity<VM : BaseViewModel> : DBActivityEx<VM>() {

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null

    @CallSuper
    override fun initializeData() {
        viewModel.init(userId, appSsid)
    }
}