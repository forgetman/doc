package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction

/**
 * 外部浏览器
 */
val IntentAction.Companion.browser: BrowserAction
    get() = BrowserAction()

class BrowserAction internal constructor() {

    fun launch(url: String) {
        val intent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(url.toUri())

        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("BrowserAction", e)
        }
    }
}