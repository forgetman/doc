package pretimmediat.stats

import android.app.Application

interface StatsOption {
    fun init(app: Application)
    fun onEvent(eventName: String, userId: String? = null, appSsid: String? = null, map: HashMap<String, String>? = null)
}