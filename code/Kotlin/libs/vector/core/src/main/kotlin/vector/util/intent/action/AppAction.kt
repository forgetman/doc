package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction

/**
 * 指定打开App的场景, 如支付宝, 微信
 */
val IntentAction.Companion.app: AppAction
    get() = AppAction()

class AppAction internal constructor() {

    fun launch(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("AppAction", e)
        }
    }
}