package pretimmediat.fragment.base.databinding

import androidx.annotation.CallSuper
import inject.annotation.creator.Extra
import pretimmediat.delegate.ServiceFlagDelegate
import pretimmediat.viewmodel.BaseViewModel
import vector.app.databinding.frag.DBFragEx

/**
 * @author yuansui
 * @since 2024/7/16
 */
abstract class BaseDBFrag<VM : BaseViewModel> : DBFragEx<VM>(), ServiceFlagDelegate {

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null

    @CallSuper
    override fun initializeData() {
        viewModel.init(userId, appSsid)
    }
}