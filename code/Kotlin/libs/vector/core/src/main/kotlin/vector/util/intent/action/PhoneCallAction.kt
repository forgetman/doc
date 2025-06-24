package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction

val IntentAction.Companion.phoneCall: PhoneCallAction
    get() = PhoneCallAction()

class PhoneCallAction internal constructor() {

    fun launch(num: String) {
        val data = "tel:$num".toUri()
        val intent = Intent(Intent.ACTION_DIAL)
            .setData(data)

        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("PhoneCallAction", e)
        }
    }
}