package pretimmediat.fragment.base

import inject.annotation.creator.Extra
import pretimmediat.delegate.ServiceFlagDelegate
import vector.app.frag.SimpleFragEx

abstract class BaseFrag : SimpleFragEx(), ServiceFlagDelegate {

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null
}