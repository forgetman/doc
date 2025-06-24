package pretimmediat.fragment.base.databinding

import inject.annotation.creator.Extra
import pretimmediat.delegate.ServiceFlagDelegate
import vector.app.databinding.frag.SimpleDBFragEx

abstract class BaseSimpleDBFrag : SimpleDBFragEx(), ServiceFlagDelegate {

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null
}