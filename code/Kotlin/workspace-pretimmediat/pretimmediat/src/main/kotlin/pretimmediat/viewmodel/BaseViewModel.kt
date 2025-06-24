package pretimmediat.viewmodel

import android.app.Application
import vector.app.viewmodel.ViewModelEx

abstract class BaseViewModel(app: Application) : ViewModelEx(app) {

    protected var userId: String? = null
        private set
    protected var appSsid: String? = null
        private set

    fun init(userId: String?, appSsid: String?) {
        this.userId = userId
        this.appSsid = appSsid
    }
}