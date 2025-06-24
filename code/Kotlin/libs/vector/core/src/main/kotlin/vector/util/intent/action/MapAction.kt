package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import sugar.ext.self
import vector.util.Launcher
import vector.util.intent.IntentAction

val IntentAction.Companion.map: MapAction
    get() = MapAction()

class MapAction internal constructor() {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var name: String? = null

    private val intent: Intent
        get() {
            val buffer = StringBuffer()
                .append("geo:")
                .append(latitude)
                .append(",")
                .append(longitude)

            if (!name.isNullOrEmpty()) buffer.append("?q=").append(name)

            val uri = buffer.toString().toUri()
            return Intent(Intent.ACTION_VIEW, uri)
        }

    /**
     * 纬度
     */
    fun latitude(lat: Double) = self { latitude = lat }

    /**
     * 经度
     */
    fun longitude(lon: Double) = self { longitude = lon }

    /**
     * 地点名称
     */
    fun name(n: String) = self { name = n }

    fun launch() {
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("MapAction", e)
        }
    }
}