package pretimmediat.activity.base.databinding

import inject.annotation.creator.Extra
import vector.app.databinding.activity.SimpleDBActivityEx

abstract class BaseSimpleDBActivity : SimpleDBActivityEx() {
    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null
}