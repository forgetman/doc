package pretimmediat.stats

import android.app.Application
import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import logger.L
import pretimmediat.property.Properties
import vector.datastore.preference.sync

/**
 * AppsFlyer统计
 */
class AppsFlyerStats : StatsOption {
    companion object {
        private const val LOG_TAG = "AppsFlyerStats"
        private const val APP_ID = "GpjnRkikt9YJUXKNzapz2g"
    }

    private lateinit var context: Context

    override fun init(app: Application) {
        context = app

        AppsFlyerLib.getInstance().init(APP_ID, null, app)
        AppsFlyerLib.getInstance().start(app, APP_ID, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                L.d(LOG_TAG, "onSuccess")
                val uid: String? = AppsFlyerLib.getInstance().getAppsFlyerUID(app)
                L.d(LOG_TAG, "onSuccess, uid = $uid")
                uid?.let { id ->
                    Properties.afId.sync().put(id)
                }
            }

            override fun onError(i: Int, s: String) {
                L.d(LOG_TAG, "onError, code = $i, desc = $s")
            }
        })
    }

    override fun onEvent(
        eventName: String,
        userId: String?,
        appSsid: String?,
        map: HashMap<String, String>?
    ) {
        val m = HashMap<String, Any>()
        map?.forEach { (key, value) ->
            m[key] = value
        }
        AppsFlyerLib.getInstance().logEvent(context, eventName, m)
    }
}