package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.appContext
import vector.util.Launcher
import vector.util.intent.IntentAction

val IntentAction.Companion.appStore: AppStoreIntentAction
    get() = AppStoreIntentAction()

class AppStoreIntentAction internal constructor() {

    fun launch(url: String? = null) {
        val uri = url?.toUri() ?: ("market://details?id=" + appContext.packageName).toUri()
        val intent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(uri)
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("AppStoreIntentAction", e)
        }
    }
}