package pretimmediat.stats

import android.app.Application
import android.content.Context
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger

/**
 * facebook统计
 */
class FaceBookStats : StatsOption {
    private lateinit var context: Context

    override fun init(app: Application) {
        context = app
        AppEventsLogger.activateApp(app)
    }

    override fun onEvent(
        eventName: String,
        userId: String?,
        appSsid: String?,
        map: HashMap<String, String>?
    ) {
        val logger = AppEventsLogger.newLogger(context)
        if (map != null) {
            val bundle = Bundle()
            map.forEach { (key, value) ->
                bundle.putString(key, value)
            }
            logger.logEvent(eventName, bundle)
        } else {
            logger.logEvent(eventName)
        }
    }
}