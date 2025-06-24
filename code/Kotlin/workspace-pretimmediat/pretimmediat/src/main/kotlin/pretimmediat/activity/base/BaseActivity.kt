package pretimmediat.activity.base

import inject.annotation.creator.Extra
import vector.app.activity.SimpleActivityEx

abstract class BaseActivity : SimpleActivityEx() {

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null
}