package vector.util.intent.action

import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import logger.L
import vector.appContext
import vector.util.Launcher
import vector.util.intent.IntentAction

val IntentAction.Companion.appSetup: AppSetupAction
    get() = AppSetupAction()

class AppSetupAction internal constructor() {

    fun launch() {
        val uri = ("package:" + appContext.packageName).toUri()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("AppSetupAction", e)
        }
    }
}